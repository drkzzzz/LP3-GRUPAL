package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.*;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.exception.StockInsuficienteException;
import DrinkGo.DrinkGo_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para transferencias entre almacenes (RF-INV-007).
 * - Descontar por FIFO en origen
 * - Registrar movimiento salida en origen
 * - Registrar movimiento entrada en destino
 * - Mantener trazabilidad completa
 * - Usar @Transactional obligatorio
 */
@Service
public class TransferenciaInventarioService {

    private final TransferenciaInventarioRepository transferenciaRepository;
    private final StockInventarioRepository stockRepository;
    private final LoteInventarioRepository loteRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final AlertaInventarioService alertaService;

    public TransferenciaInventarioService(TransferenciaInventarioRepository transferenciaRepository,
                                           StockInventarioRepository stockRepository,
                                           LoteInventarioRepository loteRepository,
                                           MovimientoInventarioRepository movimientoRepository,
                                           ProductoRepository productoRepository,
                                           AlmacenRepository almacenRepository,
                                           AlertaInventarioService alertaService) {
        this.transferenciaRepository = transferenciaRepository;
        this.stockRepository = stockRepository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.almacenRepository = almacenRepository;
        this.alertaService = alertaService;
    }

    /** Listar todas las transferencias del negocio */
    public List<TransferenciaInventarioResponse> listar(Long negocioId) {
        return transferenciaRepository.findByNegocioIdOrderByCreadoEnDesc(negocioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /** Obtener transferencia por ID */
    public TransferenciaInventarioResponse obtenerPorId(Long id, Long negocioId) {
        TransferenciaInventario trans = transferenciaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transferencia de inventario", id));
        return convertirAResponse(trans);
    }

    /** Crear transferencia (estado borrador) */
    @Transactional
    public TransferenciaInventarioResponse crear(TransferenciaInventarioRequest request,
                                                  Long negocioId, Long usuarioId) {
        // Validar almacenes pertenecen al negocio
        Almacen origen = almacenRepository.findByIdAndNegocioId(request.getAlmacenOrigenId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén origen", request.getAlmacenOrigenId()));

        Almacen destino = almacenRepository.findByIdAndNegocioId(request.getAlmacenDestinoId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén destino", request.getAlmacenDestinoId()));

        if (origen.getId().equals(destino.getId())) {
            throw new OperacionInvalidaException("El almacén origen y destino no pueden ser el mismo");
        }

        // Generar número de transferencia
        String numero = "TRF-" + negocioId + "-" + System.currentTimeMillis();

        TransferenciaInventario transferencia = new TransferenciaInventario();
        transferencia.setNegocioId(negocioId);
        transferencia.setNumeroTransferencia(numero);
        transferencia.setAlmacenOrigen(origen);
        transferencia.setAlmacenDestino(destino);
        transferencia.setEstado(TransferenciaInventario.EstadoTransferencia.borrador);
        transferencia.setSolicitadoPor(usuarioId);
        transferencia.setNotas(request.getNotas());
        transferencia.setSolicitadoEn(LocalDateTime.now());
        transferencia.setCreadoEn(LocalDateTime.now());

        // Agregar detalles
        List<DetalleTransferenciaInventario> detalles = new ArrayList<>();
        for (DetalleTransferenciaRequest detReq : request.getDetalles()) {
            Producto producto = productoRepository.findByIdAndNegocioId(detReq.getProductoId(), negocioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto", detReq.getProductoId()));

            LoteInventario lote = null;
            if (detReq.getLoteId() != null) {
                lote = loteRepository.findByIdAndNegocioId(detReq.getLoteId(), negocioId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Lote", detReq.getLoteId()));
            }

            DetalleTransferenciaInventario detalle = new DetalleTransferenciaInventario();
            detalle.setTransferencia(transferencia);
            detalle.setProducto(producto);
            detalle.setLote(lote);
            detalle.setCantidadSolicitada(detReq.getCantidadSolicitada());
            detalle.setNotas(detReq.getNotas());
            detalles.add(detalle);
        }
        transferencia.setDetalles(detalles);

        TransferenciaInventario guardada = transferenciaRepository.save(transferencia);
        return convertirAResponse(guardada);
    }

    /**
     * Despachar transferencia: ejecuta FIFO en origen y registra movimientos.
     * Cambia estado a 'en_transito'.
     */
    @Transactional
    public TransferenciaInventarioResponse despachar(Long id, Long negocioId, Long usuarioId) {
        TransferenciaInventario trans = transferenciaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transferencia de inventario", id));

        if (trans.getEstado() != TransferenciaInventario.EstadoTransferencia.borrador &&
            trans.getEstado() != TransferenciaInventario.EstadoTransferencia.pendiente) {
            throw new OperacionInvalidaException(
                    "Solo se pueden despachar transferencias en estado 'borrador' o 'pendiente'. Estado actual: " +
                    trans.getEstado().name());
        }

        // Para cada detalle: descontar FIFO en origen y registrar movimiento de salida
        for (DetalleTransferenciaInventario detalle : trans.getDetalles()) {
            Long productoId = detalle.getProductoId();
            Long almacenOrigenId = trans.getAlmacenOrigenId();
            int cantidad = detalle.getCantidadSolicitada();

            // Validar disponibilidad
            Optional<StockInventario> stockOpt = stockRepository
                    .findByProductoIdAndAlmacenIdAndNegocioId(productoId, almacenOrigenId, negocioId);

            if (stockOpt.isEmpty() || stockOpt.get().getCantidadEnMano() < cantidad) {
                int disponible = stockOpt.map(StockInventario::getCantidadEnMano).orElse(0);
                throw new StockInsuficienteException(
                        "Stock insuficiente para producto ID " + productoId +
                        " en almacén origen. Disponible: " + disponible + ", Solicitado: " + cantidad);
            }

            // Descontar FIFO de lotes en origen
            descontarFIFOOrigen(negocioId, productoId, almacenOrigenId, cantidad);

            // Actualizar stock origen (reducir)
            StockInventario stockOrigen = stockOpt.get();
            stockOrigen.setCantidadEnMano(stockOrigen.getCantidadEnMano() - cantidad);
            stockOrigen.setUltimoMovimientoEn(LocalDateTime.now());
            stockRepository.save(stockOrigen);

            // Registrar movimiento de salida en origen
            registrarMovimiento(negocioId, detalle.getProducto(),
                    trans.getAlmacenOrigen(), null,
                    MovimientoInventario.TipoMovimiento.salida_transferencia,
                    cantidad, null,
                    "transferencia_inventario", trans.getId(),
                    "Transferencia " + trans.getNumeroTransferencia() + " - Salida", usuarioId);

            detalle.setCantidadEnviada(cantidad);
        }

        trans.setEstado(TransferenciaInventario.EstadoTransferencia.en_transito);
        trans.setDespachadoEn(LocalDateTime.now());

        TransferenciaInventario guardada = transferenciaRepository.save(trans);

        // Verificar alertas en almacén origen
        Almacen origen = trans.getAlmacenOrigen();
        for (DetalleTransferenciaInventario detalle : trans.getDetalles()) {
            alertaService.verificarAlertas(negocioId, detalle.getProducto(), origen);
        }

        return convertirAResponse(guardada);
    }

    /**
     * Recibir transferencia: registra entrada en destino.
     * Cambia estado a 'recibida'.
     */
    @Transactional
    public TransferenciaInventarioResponse recibir(Long id, Long negocioId, Long usuarioId) {
        TransferenciaInventario trans = transferenciaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transferencia de inventario", id));

        if (trans.getEstado() != TransferenciaInventario.EstadoTransferencia.en_transito) {
            throw new OperacionInvalidaException(
                    "Solo se pueden recibir transferencias en estado 'en_transito'. Estado actual: " +
                    trans.getEstado().name());
        }

        // Para cada detalle: registrar entrada en destino y crear lote
        for (DetalleTransferenciaInventario detalle : trans.getDetalles()) {
            int cantidadRecibida = detalle.getCantidadEnviada() != null ?
                    detalle.getCantidadEnviada() : detalle.getCantidadSolicitada();

            // Actualizar stock destino (incrementar, crear si no existe)
            actualizarStockDestino(negocioId, detalle.getProducto(),
                    trans.getAlmacenDestino(), cantidadRecibida);

            // Crear lote en destino para mantener consistencia FIFO
            LoteInventario loteDestino = crearLoteDestino(negocioId, detalle.getProducto(),
                    trans.getAlmacenDestino(), cantidadRecibida, detalle.getLote(),
                    trans.getNumeroTransferencia());

            // Registrar movimiento de entrada en destino con referencia al nuevo lote
            registrarMovimiento(negocioId, detalle.getProducto(),
                    trans.getAlmacenDestino(), loteDestino,
                    MovimientoInventario.TipoMovimiento.entrada_transferencia,
                    cantidadRecibida, loteDestino.getPrecioCompra(),
                    "transferencia_inventario", trans.getId(),
                    "Transferencia " + trans.getNumeroTransferencia() + " - Entrada", usuarioId);

            detalle.setCantidadRecibida(cantidadRecibida);
        }

        trans.setEstado(TransferenciaInventario.EstadoTransferencia.recibida);
        trans.setRecibidoPor(usuarioId);
        trans.setRecibidoEn(LocalDateTime.now());

        TransferenciaInventario guardada = transferenciaRepository.save(trans);
        return convertirAResponse(guardada);
    }

    /**
     * Aprobar transferencia: cambia estado de 'borrador' a 'pendiente'.
     * Registra quién aprobó y cuándo. Típicamente lo hace un supervisor o gerente.
     */
    @Transactional
    public TransferenciaInventarioResponse aprobar(Long id, Long negocioId, Long usuarioId) {
        TransferenciaInventario trans = transferenciaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transferencia de inventario", id));

        if (trans.getEstado() != TransferenciaInventario.EstadoTransferencia.borrador) {
            throw new OperacionInvalidaException(
                    "Solo se pueden aprobar transferencias en estado 'borrador'. Estado actual: " +
                    trans.getEstado().name());
        }

        trans.setEstado(TransferenciaInventario.EstadoTransferencia.pendiente);
        trans.setAprobadoPor(usuarioId);
        trans.setAprobadoEn(LocalDateTime.now());

        TransferenciaInventario guardada = transferenciaRepository.save(trans);
        return convertirAResponse(guardada);
    }

    /**
     * Borrado lógico: cambiar estado a 'cancelada'.
     * Solo se pueden cancelar transferencias en estado 'borrador' o 'pendiente'.
     */
    @Transactional
    public void cancelar(Long id, Long negocioId) {
        TransferenciaInventario trans = transferenciaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transferencia de inventario", id));

        if (trans.getEstado() != TransferenciaInventario.EstadoTransferencia.borrador &&
            trans.getEstado() != TransferenciaInventario.EstadoTransferencia.pendiente) {
            throw new OperacionInvalidaException(
                    "Solo se pueden cancelar transferencias en estado 'borrador' o 'pendiente'. Estado actual: " +
                    trans.getEstado().name());
        }

        trans.setEstado(TransferenciaInventario.EstadoTransferencia.cancelada);
        transferenciaRepository.save(trans);
    }

    // ── Métodos auxiliares ──

    private void descontarFIFOOrigen(Long negocioId, Long productoId, Long almacenId, int cantidadADescontar) {
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
                    "No hay suficientes lotes disponibles. Faltante: " + restante);
        }
    }

    /**
     * Crea un lote en el almacén destino al recibir una transferencia.
     * Hereda precio_compra y fecha_vencimiento del lote origen si existe.
     */
    private LoteInventario crearLoteDestino(Long negocioId, Producto producto, Almacen almacen,
                                             int cantidad, LoteInventario loteOrigen,
                                             String numeroTransferencia) {
        LoteInventario lote = new LoteInventario();
        lote.setNegocioId(negocioId);
        lote.setProducto(producto);
        lote.setAlmacen(almacen);
        lote.setNumeroLote("TRF-" + numeroTransferencia + "-" + producto.getId() + "-" + System.currentTimeMillis());
        lote.setCantidadInicial(cantidad);
        lote.setCantidadRestante(cantidad);
        lote.setFechaRecepcion(java.time.LocalDate.now());
        lote.setEstado(LoteInventario.EstadoLote.disponible);
        lote.setCreadoEn(LocalDateTime.now());

        // Heredar datos del lote origen si existe
        if (loteOrigen != null) {
            lote.setPrecioCompra(loteOrigen.getPrecioCompra());
            lote.setFechaFabricacion(loteOrigen.getFechaFabricacion());
            lote.setFechaVencimiento(loteOrigen.getFechaVencimiento());
            lote.setProveedorId(loteOrigen.getProveedorId());
        } else {
            lote.setPrecioCompra(java.math.BigDecimal.ZERO);
        }

        lote.setNotas("Lote creado por transferencia " + numeroTransferencia);
        return loteRepository.save(lote);
    }

    private void actualizarStockDestino(Long negocioId, Producto producto, Almacen almacen, int cantidad) {
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

    private void registrarMovimiento(Long negocioId, Producto producto, Almacen almacen,
                                      LoteInventario lote,
                                      MovimientoInventario.TipoMovimiento tipo,
                                      int cantidad, java.math.BigDecimal costoUnitario,
                                      String tipoReferencia, Long referenciaId,
                                      String motivo, Long usuarioId) {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setNegocioId(negocioId);
        mov.setProducto(producto);
        mov.setAlmacen(almacen);
        mov.setLote(lote);
        mov.setTipoMovimiento(tipo);
        mov.setCantidad(cantidad);
        mov.setCostoUnitario(costoUnitario);
        mov.setTipoReferencia(tipoReferencia);
        mov.setReferenciaId(referenciaId);
        mov.setMotivo(motivo);
        mov.setRealizadoPor(usuarioId);
        mov.setCreadoEn(LocalDateTime.now());
        movimientoRepository.save(mov);
    }

    private TransferenciaInventarioResponse convertirAResponse(TransferenciaInventario trans) {
        TransferenciaInventarioResponse resp = new TransferenciaInventarioResponse();
        resp.setId(trans.getId());
        resp.setNegocioId(trans.getNegocioId());
        resp.setNumeroTransferencia(trans.getNumeroTransferencia());
        resp.setAlmacenOrigenId(trans.getAlmacenOrigenId());
        resp.setAlmacenOrigenNombre(trans.getAlmacenOrigen() != null ? trans.getAlmacenOrigen().getNombre() : null);
        resp.setAlmacenDestinoId(trans.getAlmacenDestinoId());
        resp.setAlmacenDestinoNombre(trans.getAlmacenDestino() != null ? trans.getAlmacenDestino().getNombre() : null);
        resp.setEstado(trans.getEstado() != null ? trans.getEstado().name() : null);
        resp.setSolicitadoPor(trans.getSolicitadoPor());
        resp.setAprobadoPor(trans.getAprobadoPor());
        resp.setRecibidoPor(trans.getRecibidoPor());
        resp.setNotas(trans.getNotas());
        resp.setSolicitadoEn(trans.getSolicitadoEn());
        resp.setAprobadoEn(trans.getAprobadoEn());
        resp.setDespachadoEn(trans.getDespachadoEn());
        resp.setRecibidoEn(trans.getRecibidoEn());
        resp.setCreadoEn(trans.getCreadoEn());
        resp.setActualizadoEn(trans.getActualizadoEn());

        if (trans.getDetalles() != null) {
            resp.setDetalles(trans.getDetalles().stream()
                    .map(this::convertirDetalleAResponse)
                    .collect(Collectors.toList()));
        }

        return resp;
    }

    private DetalleTransferenciaResponse convertirDetalleAResponse(DetalleTransferenciaInventario det) {
        DetalleTransferenciaResponse resp = new DetalleTransferenciaResponse();
        resp.setId(det.getId());
        resp.setProductoId(det.getProductoId());
        resp.setProductoNombre(det.getProducto() != null ? det.getProducto().getNombre() : null);
        resp.setLoteId(det.getLoteId());
        resp.setNumeroLote(det.getLote() != null ? det.getLote().getNumeroLote() : null);
        resp.setCantidadSolicitada(det.getCantidadSolicitada());
        resp.setCantidadEnviada(det.getCantidadEnviada());
        resp.setCantidadRecibida(det.getCantidadRecibida());
        resp.setNotas(det.getNotas());
        return resp;
    }
}
