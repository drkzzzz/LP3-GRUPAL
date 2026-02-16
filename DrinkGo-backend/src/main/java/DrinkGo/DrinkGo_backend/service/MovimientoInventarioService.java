package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.MovimientoInventarioRequest;
import DrinkGo.DrinkGo_backend.dto.MovimientoInventarioResponse;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para movimientos de inventario (RF-INV-006).
 * Registra cada entrada y salida con referencia al lote utilizado.
 * Valida disponibilidad antes de salida.
 * Los movimientos son inmutables (no se editan ni eliminan).
 */
@Service
public class MovimientoInventarioService {

    private static final Set<String> TIPOS_ENTRADA = Set.of(
            "entrada_compra", "entrada_devolucion", "entrada_transferencia",
            "ajuste_entrada", "stock_inicial", "entrada_produccion"
    );

    private static final Set<String> TIPOS_SALIDA = Set.of(
            "salida_venta", "salida_devolucion", "salida_transferencia",
            "ajuste_salida", "merma", "rotura", "vencimiento", "salida_produccion"
    );

    private final MovimientoInventarioRepository movimientoRepository;
    private final StockInventarioRepository stockRepository;
    private final LoteInventarioRepository loteRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final AlertaInventarioService alertaService;

    public MovimientoInventarioService(MovimientoInventarioRepository movimientoRepository,
                                        StockInventarioRepository stockRepository,
                                        LoteInventarioRepository loteRepository,
                                        ProductoRepository productoRepository,
                                        AlmacenRepository almacenRepository,
                                        AlertaInventarioService alertaService) {
        this.movimientoRepository = movimientoRepository;
        this.stockRepository = stockRepository;
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
        this.almacenRepository = almacenRepository;
        this.alertaService = alertaService;
    }

    /** Listar todos los movimientos del negocio */
    public List<MovimientoInventarioResponse> listar(Long negocioId) {
        return movimientoRepository.findByNegocioIdOrderByCreadoEnDesc(negocioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /** Obtener movimiento por ID */
    public MovimientoInventarioResponse obtenerPorId(Long id, Long negocioId) {
        MovimientoInventario mov = movimientoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento de inventario", id));
        return convertirAResponse(mov);
    }

    /**
     * Registrar movimiento de inventario (ajuste manual).
     * Para entradas: incrementa stock.
     * Para salidas: usa FIFO para descontar de lotes, valida disponibilidad.
     */
    @Transactional
    public MovimientoInventarioResponse crear(MovimientoInventarioRequest request,
                                               Long negocioId, Long usuarioId) {
        // Validar tipo de movimiento
        MovimientoInventario.TipoMovimiento tipo;
        try {
            tipo = MovimientoInventario.TipoMovimiento.valueOf(request.getTipoMovimiento());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException(
                    "Tipo de movimiento inválido: " + request.getTipoMovimiento() +
                    ". Valores permitidos: " + String.join(", ",
                    TIPOS_ENTRADA) + ", " + String.join(", ", TIPOS_SALIDA));
        }

        // Validar producto pertenece al negocio
        Producto producto = productoRepository.findByIdAndNegocioId(request.getProductoId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", request.getProductoId()));

        // Validar almacén pertenece al negocio
        Almacen almacen = almacenRepository.findByIdAndNegocioId(request.getAlmacenId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén", request.getAlmacenId()));

        // Validar lote si se proporcionó
        LoteInventario lote = null;
        if (request.getLoteId() != null) {
            lote = loteRepository.findByIdAndNegocioId(request.getLoteId(), negocioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Lote de inventario", request.getLoteId()));
        }

        boolean esEntrada = TIPOS_ENTRADA.contains(request.getTipoMovimiento());
        boolean esSalida = TIPOS_SALIDA.contains(request.getTipoMovimiento());

        if (esSalida) {
            // Validar disponibilidad antes de salida
            validarDisponibilidadSalida(negocioId, request.getProductoId(),
                    request.getAlmacenId(), request.getCantidad());

            // Descontar FIFO de lotes
            descontarFIFO(negocioId, request.getProductoId(), request.getAlmacenId(),
                    request.getCantidad());

            // Actualizar stock (reducir)
            actualizarStock(negocioId, request.getProductoId(), request.getAlmacenId(),
                    -request.getCantidad());
        } else if (esEntrada) {
            // Actualizar stock (incrementar)
            actualizarStock(negocioId, request.getProductoId(), request.getAlmacenId(),
                    request.getCantidad());

            // Crear lote automático para mantener consistencia stock/lotes (FIFO)
            if (lote == null) {
                lote = crearLoteAutomatico(negocioId, producto, almacen,
                        request.getCantidad(), request.getCostoUnitario(),
                        request.getTipoMovimiento(), request.getMotivo());
            } else {
                // Si se proporcionó lote, incrementar su cantidad
                lote.setCantidadRestante(lote.getCantidadRestante() + request.getCantidad());
                lote.setCantidadInicial(lote.getCantidadInicial() + request.getCantidad());
                loteRepository.save(lote);
            }
        }

        // Registrar el movimiento
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setNegocioId(negocioId);
        movimiento.setProducto(producto);
        movimiento.setAlmacen(almacen);
        movimiento.setLote(lote);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(request.getCantidad());
        movimiento.setCostoUnitario(request.getCostoUnitario());
        movimiento.setTipoReferencia(request.getTipoReferencia());
        movimiento.setReferenciaId(request.getReferenciaId());
        movimiento.setMotivo(request.getMotivo());
        movimiento.setRealizadoPor(usuarioId);
        movimiento.setCreadoEn(LocalDateTime.now());

        MovimientoInventario guardado = movimientoRepository.save(movimiento);

        // Verificar alertas automáticas
        alertaService.verificarAlertas(negocioId, producto, almacen);

        return convertirAResponse(guardado);
    }

    // ── Métodos auxiliares ──

    /**
     * Crea un lote automático al registrar un movimiento de entrada.
     * Garantiza que todo stock tenga respaldo en lotes para mantener FIFO consistente.
     */
    private LoteInventario crearLoteAutomatico(Long negocioId, Producto producto, Almacen almacen,
                                                int cantidad, java.math.BigDecimal costoUnitario,
                                                String tipoMovimiento, String motivo) {
        LoteInventario lote = new LoteInventario();
        lote.setNegocioId(negocioId);
        lote.setProducto(producto);
        lote.setAlmacen(almacen);
        lote.setNumeroLote("MOV-" + tipoMovimiento.toUpperCase() + "-" + System.currentTimeMillis());
        lote.setCantidadInicial(cantidad);
        lote.setCantidadRestante(cantidad);
        lote.setPrecioCompra(costoUnitario != null ? costoUnitario : java.math.BigDecimal.ZERO);
        lote.setFechaRecepcion(java.time.LocalDate.now());
        lote.setEstado(LoteInventario.EstadoLote.disponible);
        lote.setNotas("Lote generado automáticamente por movimiento: " +
                (motivo != null ? motivo : tipoMovimiento));
        lote.setCreadoEn(LocalDateTime.now());
        return loteRepository.save(lote);
    }

    private void validarDisponibilidadSalida(Long negocioId, Long productoId,
                                              Long almacenId, int cantidad) {
        Optional<StockInventario> stockOpt = stockRepository
                .findByProductoIdAndAlmacenIdAndNegocioId(productoId, almacenId, negocioId);

        if (stockOpt.isEmpty() || stockOpt.get().getCantidadEnMano() < cantidad) {
            int disponible = stockOpt.map(StockInventario::getCantidadEnMano).orElse(0);
            throw new StockInsuficienteException(
                    "Stock insuficiente para la salida. Disponible: " + disponible +
                    ", Solicitado: " + cantidad);
        }
    }

    private void descontarFIFO(Long negocioId, Long productoId, Long almacenId, int cantidadADescontar) {
        List<LoteInventario> lotesDisponibles = loteRepository
                .findLotesFIFODisponibles(
                        productoId, almacenId, negocioId,
                        LoteInventario.EstadoLote.disponible, 0);

        int restante = cantidadADescontar;
        for (LoteInventario lote : lotesDisponibles) {
            if (restante <= 0) break;

            int descontar = Math.min(restante, lote.getCantidadRestante());
            lote.setCantidadRestante(lote.getCantidadRestante() - descontar);

            if (lote.getCantidadRestante() == 0) {
                lote.setEstado(LoteInventario.EstadoLote.agotado);
            }

            loteRepository.save(lote);
            restante -= descontar;
        }

        if (restante > 0) {
            throw new StockInsuficienteException(
                    "No hay suficientes lotes disponibles para cubrir la cantidad solicitada");
        }
    }

    private void actualizarStock(Long negocioId, Long productoId, Long almacenId, int delta) {
        Optional<StockInventario> stockOpt = stockRepository
                .findByProductoIdAndAlmacenIdAndNegocioId(productoId, almacenId, negocioId);

        StockInventario stock;
        if (stockOpt.isPresent()) {
            stock = stockOpt.get();
            int nuevoStock = stock.getCantidadEnMano() + delta;
            if (nuevoStock < 0) {
                throw new StockInsuficienteException("El stock resultante sería negativo");
            }
            stock.setCantidadEnMano(nuevoStock);
        } else {
            if (delta < 0) {
                throw new StockInsuficienteException("No existe registro de stock para este producto en este almacén");
            }
            Producto producto = productoRepository.findById(productoId).orElse(null);
            Almacen almacen = almacenRepository.findById(almacenId).orElse(null);
            stock = new StockInventario();
            stock.setNegocioId(negocioId);
            stock.setProducto(producto);
            stock.setAlmacen(almacen);
            stock.setCantidadEnMano(delta);
            stock.setCantidadReservada(0);
            stock.setCreadoEn(LocalDateTime.now());
        }
        stock.setUltimoMovimientoEn(LocalDateTime.now());
        stockRepository.save(stock);
    }

    private MovimientoInventarioResponse convertirAResponse(MovimientoInventario mov) {
        MovimientoInventarioResponse resp = new MovimientoInventarioResponse();
        resp.setId(mov.getId());
        resp.setNegocioId(mov.getNegocioId());
        resp.setProductoId(mov.getProductoId());
        resp.setProductoNombre(mov.getProducto() != null ? mov.getProducto().getNombre() : null);
        resp.setAlmacenId(mov.getAlmacenId());
        resp.setAlmacenNombre(mov.getAlmacen() != null ? mov.getAlmacen().getNombre() : null);
        resp.setLoteId(mov.getLoteId());
        resp.setNumeroLote(mov.getLote() != null ? mov.getLote().getNumeroLote() : null);
        resp.setTipoMovimiento(mov.getTipoMovimiento() != null ? mov.getTipoMovimiento().name() : null);
        resp.setCantidad(mov.getCantidad());
        resp.setCostoUnitario(mov.getCostoUnitario());
        resp.setTipoReferencia(mov.getTipoReferencia());
        resp.setReferenciaId(mov.getReferenciaId());
        resp.setMotivo(mov.getMotivo());
        resp.setRealizadoPor(mov.getRealizadoPor());
        resp.setCreadoEn(mov.getCreadoEn());
        return resp;
    }
}
