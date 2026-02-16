package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.LoteInventarioRequest;
import DrinkGo.DrinkGo_backend.dto.LoteInventarioResponse;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.exception.StockInsuficienteException;
import DrinkGo.DrinkGo_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de lotes de inventario con FIFO estricto (RF-INV-002..003).
 * - Ordenar por fecha_recepcion ASC (FIFO)
 * - Solo lotes con estado='disponible' y cantidad_restante > 0
 * - Consumir lote más antiguo primero
 * - Marcar agotado cuando cantidad_restante llega a 0
 * - Usar @Transactional obligatorio
 */
@Service
public class LoteInventarioService {

    private final LoteInventarioRepository loteRepository;
    private final StockInventarioRepository stockRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final AlertaInventarioService alertaService;

    public LoteInventarioService(LoteInventarioRepository loteRepository,
                                  StockInventarioRepository stockRepository,
                                  MovimientoInventarioRepository movimientoRepository,
                                  ProductoRepository productoRepository,
                                  AlmacenRepository almacenRepository,
                                  AlertaInventarioService alertaService) {
        this.loteRepository = loteRepository;
        this.stockRepository = stockRepository;
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.almacenRepository = almacenRepository;
        this.alertaService = alertaService;
    }

    /** Listar todos los lotes del negocio */
    public List<LoteInventarioResponse> listar(Long negocioId) {
        return loteRepository.findByNegocioId(negocioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /** Obtener lote por ID */
    public LoteInventarioResponse obtenerPorId(Long id, Long negocioId) {
        LoteInventario lote = loteRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote de inventario", id));
        return convertirAResponse(lote);
    }

    /**
     * Registrar entrada de lote de inventario (RF-INV-002).
     * Al crear un lote, se actualiza el stock correspondiente y se registra movimiento.
     */
    @Transactional
    public LoteInventarioResponse crear(LoteInventarioRequest request, Long negocioId, Long usuarioId) {
        // Validar producto pertenece al negocio
        Producto producto = productoRepository.findByIdAndNegocioId(request.getProductoId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", request.getProductoId()));

        // Validar almacén pertenece al negocio
        Almacen almacen = almacenRepository.findByIdAndNegocioId(request.getAlmacenId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén", request.getAlmacenId()));

        // Verificar unicidad del número de lote
        if (loteRepository.existsByNumeroLoteAndNegocioId(request.getNumeroLote(), negocioId)) {
            throw new OperacionInvalidaException(
                    "Ya existe un lote con el número '" + request.getNumeroLote() + "' en este negocio");
        }

        // Crear lote
        LoteInventario lote = new LoteInventario();
        lote.setNegocioId(negocioId);
        lote.setProducto(producto);            // Relación @ManyToOne persiste producto_id
        lote.setAlmacen(almacen);              // Relación @ManyToOne persiste almacen_id
        lote.setNumeroLote(request.getNumeroLote());
        lote.setCantidadInicial(request.getCantidadInicial());
        lote.setCantidadRestante(request.getCantidadInicial());
        lote.setPrecioCompra(request.getPrecioCompra());
        lote.setFechaFabricacion(request.getFechaFabricacion());
        lote.setFechaVencimiento(request.getFechaVencimiento());
        lote.setFechaRecepcion(request.getFechaRecepcion());
        lote.setProveedorId(request.getProveedorId());
        lote.setOrdenCompraId(request.getOrdenCompraId());
        lote.setEstado(LoteInventario.EstadoLote.disponible);
        lote.setNotas(request.getNotas());
        lote.setCreadoEn(LocalDateTime.now());

        LoteInventario guardado = loteRepository.save(lote);

        // Actualizar stock (crear si no existe)
        actualizarStockEntrada(negocioId, producto, almacen, request.getCantidadInicial());

        // Registrar movimiento de entrada
        registrarMovimiento(negocioId, producto, almacen, guardado,
                MovimientoInventario.TipoMovimiento.entrada_compra,
                request.getCantidadInicial(), request.getPrecioCompra(),
                "lote_inventario", guardado.getId(),
                "Entrada de lote " + request.getNumeroLote(), usuarioId);

        // Verificar alertas automáticas
        alertaService.verificarAlertas(negocioId, producto, almacen);

        return convertirAResponse(guardado);
    }

    /** Actualizar información del lote (no cantidades de stock) */
    @Transactional
    public LoteInventarioResponse actualizar(Long id, LoteInventarioRequest request, Long negocioId) {
        LoteInventario lote = loteRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote de inventario", id));

        // Solo se actualizan datos informativos, no cantidades
        lote.setFechaFabricacion(request.getFechaFabricacion());
        lote.setFechaVencimiento(request.getFechaVencimiento());
        lote.setPrecioCompra(request.getPrecioCompra());
        lote.setProveedorId(request.getProveedorId());
        lote.setOrdenCompraId(request.getOrdenCompraId());
        lote.setNotas(request.getNotas());

        LoteInventario guardado = loteRepository.save(lote);
        return convertirAResponse(guardado);
    }

    /**
     * Borrado lógico: cambiar estado a 'agotado'.
     * La tabla lotes_inventario tiene campo estado (ENUM) que se usa para el borrado lógico.
     */
    @Transactional
    public void eliminar(Long id, Long negocioId) {
        LoteInventario lote = loteRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote de inventario", id));

        // Descontar la cantidad restante del stock antes de agotar el lote
        int cantidadRestante = lote.getCantidadRestante();
        if (cantidadRestante > 0) {
            Optional<StockInventario> stockOpt = stockRepository
                    .findByProductoIdAndAlmacenIdAndNegocioId(
                            lote.getProductoId(), lote.getAlmacenId(), negocioId);

            if (stockOpt.isPresent()) {
                StockInventario stock = stockOpt.get();
                int nuevoStock = stock.getCantidadEnMano() - cantidadRestante;
                stock.setCantidadEnMano(Math.max(nuevoStock, 0));
                stock.setUltimoMovimientoEn(LocalDateTime.now());
                stockRepository.save(stock);
            }

            // Registrar movimiento de ajuste por eliminación de lote
            Producto producto = lote.getProducto();
            Almacen almacen = lote.getAlmacen();
            if (producto != null && almacen != null) {
                registrarMovimiento(negocioId, producto, almacen, lote,
                        MovimientoInventario.TipoMovimiento.ajuste_salida,
                        cantidadRestante, lote.getPrecioCompra(),
                        "eliminacion_lote", lote.getId(),
                        "Eliminación de lote " + lote.getNumeroLote(), null);
            }
        }

        lote.setEstado(LoteInventario.EstadoLote.agotado);
        lote.setCantidadRestante(0);
        loteRepository.save(lote);
    }

    /**
     * Consumo FIFO: descuenta inventario desde los lotes más antiguos.
     * Usado por movimientos de salida y transferencias.
     */
    @Transactional
    public void consumirFIFO(Long negocioId, Long productoId, Long almacenId,
                              int cantidadADescontar, Long usuarioId, String motivo) {
        List<LoteInventario> lotesDisponibles = loteRepository
                .findLotesFIFODisponibles(
                        productoId, almacenId, negocioId,
                        LoteInventario.EstadoLote.disponible, 0);

        int totalDisponible = lotesDisponibles.stream()
                .mapToInt(LoteInventario::getCantidadRestante).sum();

        if (totalDisponible < cantidadADescontar) {
            throw new StockInsuficienteException(
                    "Stock insuficiente. Disponible: " + totalDisponible + ", Solicitado: " + cantidadADescontar);
        }

        int restante = cantidadADescontar;
        for (LoteInventario lote : lotesDisponibles) {
            if (restante <= 0) break;

            int descontar = Math.min(restante, lote.getCantidadRestante());
            lote.setCantidadRestante(lote.getCantidadRestante() - descontar);

            // Marcar como agotado si llega a 0
            if (lote.getCantidadRestante() == 0) {
                lote.setEstado(LoteInventario.EstadoLote.agotado);
            }

            loteRepository.save(lote);
            restante -= descontar;

            // Registrar movimiento por cada lote consumido
            Producto producto = productoRepository.findById(productoId).orElse(null);
            Almacen almacen = almacenRepository.findById(almacenId).orElse(null);
            if (producto != null && almacen != null) {
                registrarMovimiento(negocioId, producto, almacen, lote,
                        MovimientoInventario.TipoMovimiento.ajuste_salida,
                        descontar, lote.getPrecioCompra(),
                        "ajuste_manual", null, motivo, usuarioId);
            }
        }

        // Actualizar stock total
        actualizarStockSalida(negocioId, productoId, almacenId, cantidadADescontar);
    }

    // ── Métodos auxiliares ──

    private void actualizarStockEntrada(Long negocioId, Producto producto, Almacen almacen, int cantidad) {
        Optional<StockInventario> stockOpt = stockRepository
                .findByProductoIdAndAlmacenIdAndNegocioId(producto.getId(), almacen.getId(), negocioId);

        StockInventario stock;
        if (stockOpt.isPresent()) {
            stock = stockOpt.get();
            stock.setCantidadEnMano(stock.getCantidadEnMano() + cantidad);
        } else {
            stock = new StockInventario();
            stock.setNegocioId(negocioId);
            stock.setProducto(producto);
            stock.setAlmacen(almacen);
            stock.setCantidadEnMano(cantidad);
            stock.setCantidadReservada(0);
            stock.setCreadoEn(LocalDateTime.now());
        }
        stock.setUltimoMovimientoEn(LocalDateTime.now());
        stockRepository.save(stock);
    }

    private void actualizarStockSalida(Long negocioId, Long productoId, Long almacenId, int cantidad) {
        StockInventario stock = stockRepository
                .findByProductoIdAndAlmacenIdAndNegocioId(productoId, almacenId, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe registro de stock para el producto " + productoId + " en el almacén " + almacenId));

        int nuevoStock = stock.getCantidadEnMano() - cantidad;
        if (nuevoStock < 0) {
            throw new StockInsuficienteException("El stock resultante sería negativo");
        }
        stock.setCantidadEnMano(nuevoStock);
        stock.setUltimoMovimientoEn(LocalDateTime.now());
        stockRepository.save(stock);
    }

    private void registrarMovimiento(Long negocioId, Producto producto, Almacen almacen,
                                      LoteInventario lote,
                                      MovimientoInventario.TipoMovimiento tipo,
                                      int cantidad, java.math.BigDecimal costoUnitario,
                                      String tipoReferencia, Long referenciaId,
                                      String motivo, Long usuarioId) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setNegocioId(negocioId);
        movimiento.setProducto(producto);     // Usar relación @ManyToOne (insertable=false en ID)
        movimiento.setAlmacen(almacen);       // Usar relación @ManyToOne (insertable=false en ID)
        movimiento.setLote(lote);             // Usar relación @ManyToOne (insertable=false en ID)
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setCostoUnitario(costoUnitario);
        movimiento.setTipoReferencia(tipoReferencia);
        movimiento.setReferenciaId(referenciaId);
        movimiento.setMotivo(motivo);
        movimiento.setRealizadoPor(usuarioId);
        movimiento.setCreadoEn(LocalDateTime.now());
        movimientoRepository.save(movimiento);
    }

    private LoteInventarioResponse convertirAResponse(LoteInventario lote) {
        LoteInventarioResponse resp = new LoteInventarioResponse();
        resp.setId(lote.getId());
        resp.setNegocioId(lote.getNegocioId());
        resp.setProductoId(lote.getProductoId());
        resp.setProductoNombre(lote.getProducto() != null ? lote.getProducto().getNombre() : null);
        resp.setAlmacenId(lote.getAlmacenId());
        resp.setAlmacenNombre(lote.getAlmacen() != null ? lote.getAlmacen().getNombre() : null);
        resp.setNumeroLote(lote.getNumeroLote());
        resp.setCantidadInicial(lote.getCantidadInicial());
        resp.setCantidadRestante(lote.getCantidadRestante());
        resp.setPrecioCompra(lote.getPrecioCompra());
        resp.setFechaFabricacion(lote.getFechaFabricacion());
        resp.setFechaVencimiento(lote.getFechaVencimiento());
        resp.setFechaRecepcion(lote.getFechaRecepcion());
        resp.setProveedorId(lote.getProveedorId());
        resp.setOrdenCompraId(lote.getOrdenCompraId());
        resp.setEstado(lote.getEstado() != null ? lote.getEstado().name() : null);
        resp.setNotas(lote.getNotas());
        resp.setCreadoEn(lote.getCreadoEn());
        resp.setActualizadoEn(lote.getActualizadoEn());
        return resp;
    }
}
