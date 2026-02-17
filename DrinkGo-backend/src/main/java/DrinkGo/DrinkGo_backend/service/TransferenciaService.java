package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.DetalleTransferenciaRequest;
import DrinkGo.DrinkGo_backend.dto.TransferenciaDespachoRequest;
import DrinkGo.DrinkGo_backend.dto.TransferenciaRecepcionRequest;
import DrinkGo.DrinkGo_backend.dto.TransferenciaRequest;
import DrinkGo.DrinkGo_backend.dto.TransferenciaUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.entity.LoteInventario.LoteEstado;
import DrinkGo.DrinkGo_backend.entity.MovimientoInventario.TipoMovimiento;
import DrinkGo.DrinkGo_backend.entity.TransferenciaInventario.TransferenciaEstado;
import DrinkGo.DrinkGo_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de transferencias entre almacenes - Bloque 5.
 * Implementa RF-INV-007: Flujo completo de origen → tránsito → destino.
 * Registra movimientos automáticos de salida y entrada.
 */
@Service
public class TransferenciaService {

    @Autowired
    private TransferenciaInventarioRepository transferenciaRepository;

    @Autowired
    private DetalleTransferenciaInventarioRepository detalleRepository;

    @Autowired
    private StockInventarioRepository stockRepository;

    @Autowired
    private LoteInventarioRepository loteRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    /**
     * Listar todas las transferencias del negocio.
     */
    public List<TransferenciaInventario> listarTransferencias(Long negocioId) {
        return transferenciaRepository.findByNegocioIdOrderByCreadoEnDesc(negocioId);
    }

    /**
     * Obtener una transferencia por ID (validando tenant).
     */
    public TransferenciaInventario obtenerTransferencia(Long negocioId, Long transferenciaId) {
        return transferenciaRepository.findByIdAndNegocioId(transferenciaId, negocioId)
                .orElseThrow(() -> new RuntimeException("Transferencia no encontrada: " + transferenciaId));
    }

    /**
     * Crear una nueva transferencia (estado: borrador).
     */
    @Transactional
    public TransferenciaInventario crearTransferencia(Long negocioId, TransferenciaRequest request) {
        // Validaciones
        if (request.getAlmacenOrigenId() == null) {
            throw new RuntimeException("El almacén de origen es obligatorio");
        }
        if (request.getAlmacenDestinoId() == null) {
            throw new RuntimeException("El almacén de destino es obligatorio");
        }
        if (request.getAlmacenOrigenId().equals(request.getAlmacenDestinoId())) {
            throw new RuntimeException("El almacén de origen y destino no pueden ser el mismo");
        }
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new RuntimeException("Debe incluir al menos un producto en la transferencia");
        }

        // Generar número de transferencia
        long count = transferenciaRepository.countByNegocioId(negocioId);
        String numeroTransferencia = String.format("TRF-%06d", count + 1);

        // Crear transferencia
        TransferenciaInventario transferencia = new TransferenciaInventario();
        transferencia.setNegocioId(negocioId);
        transferencia.setNumeroTransferencia(numeroTransferencia);
        transferencia.setAlmacenOrigenId(request.getAlmacenOrigenId());
        transferencia.setAlmacenDestinoId(request.getAlmacenDestinoId());
        transferencia.setEstado(TransferenciaEstado.borrador);
        transferencia.setNotas(request.getNotas());
        transferencia.setSolicitadoEn(LocalDateTime.now());

        TransferenciaInventario saved = transferenciaRepository.save(transferencia);

        // Crear detalles
        for (DetalleTransferenciaRequest detalleReq : request.getDetalles()) {
            if (detalleReq.getProductoId() == null) {
                throw new RuntimeException("El productoId es obligatorio en cada detalle");
            }
            if (detalleReq.getCantidadSolicitada() == null || detalleReq.getCantidadSolicitada() <= 0) {
                throw new RuntimeException("La cantidad solicitada debe ser mayor a 0");
            }

            DetalleTransferenciaInventario detalle = new DetalleTransferenciaInventario();
            detalle.setTransferenciaId(saved.getId());
            detalle.setProductoId(detalleReq.getProductoId());
            detalle.setLoteId(detalleReq.getLoteId());
            detalle.setCantidadSolicitada(detalleReq.getCantidadSolicitada());
            detalleRepository.save(detalle);
        }

        return transferenciaRepository.findByIdAndNegocioId(saved.getId(), negocioId)
                .orElse(saved);
    }

    /**
     * Despachar una transferencia: origen → tránsito.
     * Valida stock, descuenta de origen con FIFO, cambia estado a 'en_transito'.
     */
    @Transactional
    public TransferenciaInventario despacharTransferencia(Long negocioId, Long transferenciaId,
                                                          TransferenciaDespachoRequest request) {
        TransferenciaInventario transferencia = obtenerTransferencia(negocioId, transferenciaId);

        if (transferencia.getEstado() != TransferenciaEstado.borrador &&
                transferencia.getEstado() != TransferenciaEstado.pendiente) {
            throw new RuntimeException("Solo se pueden despachar transferencias en estado 'borrador' o 'pendiente'. " +
                    "Estado actual: " + transferencia.getEstado());
        }

        List<DetalleTransferenciaInventario> detalles = detalleRepository.findByTransferenciaId(transferenciaId);

        // Actualizar cantidades enviadas si se proporcionan
        if (request != null && request.getDetalles() != null) {
            for (TransferenciaDespachoRequest.DetalleDespachoItem item : request.getDetalles()) {
                DetalleTransferenciaInventario detalle = detalles.stream()
                        .filter(d -> d.getId().equals(item.getDetalleId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Detalle no encontrado: " + item.getDetalleId()));

                detalle.setCantidadEnviada(item.getCantidadEnviada());
                detalleRepository.save(detalle);
            }
            // Recargar detalles
            detalles = detalleRepository.findByTransferenciaId(transferenciaId);
        }

        // --- Descontar stock del almacén de origen ---
        for (DetalleTransferenciaInventario detalle : detalles) {
            int cantidadEnviar = detalle.getCantidadEnviada() != null
                    ? detalle.getCantidadEnviada()
                    : detalle.getCantidadSolicitada();

            if (cantidadEnviar <= 0) continue;

            // Establecer cantidad enviada
            detalle.setCantidadEnviada(cantidadEnviar);
            detalleRepository.save(detalle);

            // Verificar stock disponible
            StockInventario stock = stockRepository
                    .findByNegocioIdAndProductoIdAndAlmacenId(negocioId, detalle.getProductoId(),
                            transferencia.getAlmacenOrigenId())
                    .orElseThrow(() -> new RuntimeException("No hay stock del producto " +
                            detalle.getProductoId() + " en el almacén de origen"));

            if (stock.getCantidadEnMano() < cantidadEnviar) {
                throw new RuntimeException("Stock insuficiente para producto " + detalle.getProductoId() +
                        ". Disponible: " + stock.getCantidadEnMano() + ", Solicitado: " + cantidadEnviar);
            }

            // Descontar del lote específico o FIFO
            if (detalle.getLoteId() != null) {
                // Lote específico
                LoteInventario lote = loteRepository.findById(detalle.getLoteId())
                        .orElseThrow(() -> new RuntimeException("Lote no encontrado: " + detalle.getLoteId()));

                if (!lote.getNegocioId().equals(negocioId)) {
                    throw new RuntimeException("El lote no pertenece al negocio");
                }
                if (lote.getCantidadRestante() < cantidadEnviar) {
                    throw new RuntimeException("Cantidad insuficiente en lote " + lote.getNumeroLote());
                }

                lote.setCantidadRestante(lote.getCantidadRestante() - cantidadEnviar);
                if (lote.getCantidadRestante() == 0) {
                    lote.setEstado(LoteEstado.agotado);
                }
                loteRepository.save(lote);

                // Movimiento de salida
                crearMovimientoTransferencia(negocioId, detalle.getProductoId(),
                        transferencia.getAlmacenOrigenId(), lote.getId(),
                        TipoMovimiento.salida_transferencia, -cantidadEnviar,
                        lote.getPrecioCompra(), transferenciaId);

            } else {
                // FIFO
                descontarFIFOTransferencia(negocioId, detalle.getProductoId(),
                        transferencia.getAlmacenOrigenId(), cantidadEnviar, transferenciaId);
            }

            // Actualizar stock de origen
            stock.setCantidadEnMano(stock.getCantidadEnMano() - cantidadEnviar);
            stock.setUltimoMovimientoEn(LocalDateTime.now());
            stockRepository.save(stock);
        }

        // Cambiar estado
        transferencia.setEstado(TransferenciaEstado.en_transito);
        transferencia.setDespachadoEn(LocalDateTime.now());
        return transferenciaRepository.save(transferencia);
    }

    /**
     * Recibir una transferencia: tránsito → destino.
     * Crea nuevos lotes en destino, actualiza stock destino, cambia estado a 'recibida'.
     */
    @Transactional
    public TransferenciaInventario recibirTransferencia(Long negocioId, Long transferenciaId,
                                                         TransferenciaRecepcionRequest request) {
        TransferenciaInventario transferencia = obtenerTransferencia(negocioId, transferenciaId);

        if (transferencia.getEstado() != TransferenciaEstado.en_transito) {
            throw new RuntimeException("Solo se pueden recibir transferencias en estado 'en_transito'. " +
                    "Estado actual: " + transferencia.getEstado());
        }

        List<DetalleTransferenciaInventario> detalles = detalleRepository.findByTransferenciaId(transferenciaId);

        // Actualizar cantidades recibidas si se proporcionan
        if (request != null && request.getDetalles() != null) {
            for (TransferenciaRecepcionRequest.DetalleRecepcionItem item : request.getDetalles()) {
                DetalleTransferenciaInventario detalle = detalles.stream()
                        .filter(d -> d.getId().equals(item.getDetalleId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Detalle no encontrado: " + item.getDetalleId()));

                detalle.setCantidadRecibida(item.getCantidadRecibida());
                detalleRepository.save(detalle);
            }
            detalles = detalleRepository.findByTransferenciaId(transferenciaId);
        }

        // --- Ingresar stock al almacén de destino ---
        for (DetalleTransferenciaInventario detalle : detalles) {
            int cantidadRecibir = detalle.getCantidadRecibida() != null
                    ? detalle.getCantidadRecibida()
                    : (detalle.getCantidadEnviada() != null
                    ? detalle.getCantidadEnviada()
                    : detalle.getCantidadSolicitada());

            if (cantidadRecibir <= 0) continue;

            detalle.setCantidadRecibida(cantidadRecibir);
            detalleRepository.save(detalle);

            // Crear nuevo lote en almacén destino
            LoteInventario loteOrigen = null;
            if (detalle.getLoteId() != null) {
                loteOrigen = loteRepository.findById(detalle.getLoteId()).orElse(null);
            }

            LoteInventario nuevoLote = new LoteInventario();
            nuevoLote.setNegocioId(negocioId);
            nuevoLote.setProductoId(detalle.getProductoId());
            nuevoLote.setAlmacenId(transferencia.getAlmacenDestinoId());
            nuevoLote.setNumeroLote("TRF-" + transferenciaId + "-" + detalle.getProductoId() + "-" + System.currentTimeMillis());
            nuevoLote.setCantidadInicial(cantidadRecibir);
            nuevoLote.setCantidadRestante(cantidadRecibir);
            nuevoLote.setPrecioCompra(loteOrigen != null ? loteOrigen.getPrecioCompra() : BigDecimal.ZERO);
            nuevoLote.setFechaRecepcion(LocalDate.now());
            nuevoLote.setFechaVencimiento(loteOrigen != null ? loteOrigen.getFechaVencimiento() : null);
            nuevoLote.setFechaFabricacion(loteOrigen != null ? loteOrigen.getFechaFabricacion() : null);
            nuevoLote.setEstado(LoteEstado.disponible);
            nuevoLote.setNotas("Recibido por transferencia " + transferencia.getNumeroTransferencia());
            LoteInventario loteGuardado = loteRepository.save(nuevoLote);

            // Actualizar stock destino
            StockInventario stockDestino = stockRepository
                    .findByNegocioIdAndProductoIdAndAlmacenId(negocioId, detalle.getProductoId(),
                            transferencia.getAlmacenDestinoId())
                    .orElseGet(() -> {
                        StockInventario nuevo = new StockInventario();
                        nuevo.setNegocioId(negocioId);
                        nuevo.setProductoId(detalle.getProductoId());
                        nuevo.setAlmacenId(transferencia.getAlmacenDestinoId());
                        nuevo.setCantidadEnMano(0);
                        nuevo.setCantidadReservada(0);
                        return stockRepository.save(nuevo);
                    });

            stockDestino.setCantidadEnMano(stockDestino.getCantidadEnMano() + cantidadRecibir);
            stockDestino.setUltimoMovimientoEn(LocalDateTime.now());
            stockRepository.save(stockDestino);

            // Movimiento de entrada en destino
            crearMovimientoTransferencia(negocioId, detalle.getProductoId(),
                    transferencia.getAlmacenDestinoId(), loteGuardado.getId(),
                    TipoMovimiento.entrada_transferencia, cantidadRecibir,
                    nuevoLote.getPrecioCompra(), transferenciaId);
        }

        // Cambiar estado
        transferencia.setEstado(TransferenciaEstado.recibida);
        transferencia.setRecibidoEn(LocalDateTime.now());
        return transferenciaRepository.save(transferencia);
    }

    /**
     * Cancelar una transferencia.
     * Solo si está en estado 'borrador' o 'pendiente'.
     */
    @Transactional
    public TransferenciaInventario cancelarTransferencia(Long negocioId, Long transferenciaId) {
        TransferenciaInventario transferencia = obtenerTransferencia(negocioId, transferenciaId);

        if (transferencia.getEstado() != TransferenciaEstado.borrador &&
                transferencia.getEstado() != TransferenciaEstado.pendiente) {
            throw new RuntimeException("Solo se pueden cancelar transferencias en estado 'borrador' o 'pendiente'. " +
                    "Estado actual: " + transferencia.getEstado());
        }

        transferencia.setEstado(TransferenciaEstado.cancelada);
        return transferenciaRepository.save(transferencia);
    }

    /**
     * Actualizar una transferencia (solo en estado 'borrador').
     * Permite cambiar almacenes, notas y detalles.
     */
    @Transactional
    public TransferenciaInventario actualizarTransferencia(Long negocioId, Long transferenciaId,
                                                            TransferenciaUpdateRequest request) {
        TransferenciaInventario transferencia = obtenerTransferencia(negocioId, transferenciaId);

        if (transferencia.getEstado() != TransferenciaEstado.borrador) {
            throw new RuntimeException("Solo se pueden modificar transferencias en estado 'borrador'. " +
                    "Estado actual: " + transferencia.getEstado());
        }

        if (request.getNotas() != null) {
            transferencia.setNotas(request.getNotas());
        }
        if (request.getAlmacenOrigenId() != null) {
            transferencia.setAlmacenOrigenId(request.getAlmacenOrigenId());
        }
        if (request.getAlmacenDestinoId() != null) {
            transferencia.setAlmacenDestinoId(request.getAlmacenDestinoId());
        }

        // Validar que origen y destino no sean iguales
        if (transferencia.getAlmacenOrigenId().equals(transferencia.getAlmacenDestinoId())) {
            throw new RuntimeException("El almacén de origen y destino no pueden ser el mismo");
        }

        // Si se proporcionan nuevos detalles, reemplazar los existentes
        if (request.getDetalles() != null && !request.getDetalles().isEmpty()) {
            // Eliminar detalles anteriores
            List<DetalleTransferenciaInventario> detallesAnteriores =
                    detalleRepository.findByTransferenciaId(transferenciaId);
            detalleRepository.deleteAll(detallesAnteriores);

            // Crear nuevos detalles
            for (DetalleTransferenciaRequest detalleReq : request.getDetalles()) {
                if (detalleReq.getProductoId() == null) {
                    throw new RuntimeException("El productoId es obligatorio en cada detalle");
                }
                if (detalleReq.getCantidadSolicitada() == null || detalleReq.getCantidadSolicitada() <= 0) {
                    throw new RuntimeException("La cantidad solicitada debe ser mayor a 0");
                }

                DetalleTransferenciaInventario detalle = new DetalleTransferenciaInventario();
                detalle.setTransferenciaId(transferenciaId);
                detalle.setProductoId(detalleReq.getProductoId());
                detalle.setLoteId(detalleReq.getLoteId());
                detalle.setCantidadSolicitada(detalleReq.getCantidadSolicitada());
                detalleRepository.save(detalle);
            }
        }

        return transferenciaRepository.save(transferencia);
    }

    /**
     * Eliminar transferencia (borrado físico).
     * Solo si está en estado 'borrador' o 'cancelada'.
     * NOTA: La tabla transferencias_inventario NO tiene columna eliminado_en.
     */
    @Transactional
    public void eliminarTransferencia(Long negocioId, Long transferenciaId) {
        TransferenciaInventario transferencia = obtenerTransferencia(negocioId, transferenciaId);

        if (transferencia.getEstado() != TransferenciaEstado.borrador &&
                transferencia.getEstado() != TransferenciaEstado.cancelada) {
            throw new RuntimeException("Solo se pueden eliminar transferencias en estado 'borrador' o 'cancelada'. " +
                    "Estado actual: " + transferencia.getEstado());
        }

        transferenciaRepository.delete(transferencia);
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    private void descontarFIFOTransferencia(Long negocioId, Long productoId, Long almacenId,
                                             int cantidad, Long transferenciaId) {
        List<LoteInventario> lotes = loteRepository.findLotesDisponiblesFIFO(negocioId, productoId, almacenId);

        int stockDisponible = lotes.stream().mapToInt(LoteInventario::getCantidadRestante).sum();
        if (stockDisponible < cantidad) {
            throw new RuntimeException("Stock insuficiente para producto " + productoId +
                    ". Disponible: " + stockDisponible + ", Solicitado: " + cantidad);
        }

        int restante = cantidad;
        for (LoteInventario lote : lotes) {
            if (restante <= 0) break;

            int consumir = Math.min(restante, lote.getCantidadRestante());
            lote.setCantidadRestante(lote.getCantidadRestante() - consumir);
            if (lote.getCantidadRestante() == 0) {
                lote.setEstado(LoteEstado.agotado);
            }
            loteRepository.save(lote);

            crearMovimientoTransferencia(negocioId, productoId, almacenId, lote.getId(),
                    TipoMovimiento.salida_transferencia, -consumir,
                    lote.getPrecioCompra(), transferenciaId);

            restante -= consumir;
        }
    }

    private void crearMovimientoTransferencia(Long negocioId, Long productoId, Long almacenId,
                                               Long loteId, TipoMovimiento tipo, int cantidad,
                                               BigDecimal costoUnitario, Long transferenciaId) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setNegocioId(negocioId);
        movimiento.setProductoId(productoId);
        movimiento.setAlmacenId(almacenId);
        movimiento.setLoteId(loteId);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setCostoUnitario(costoUnitario);
        movimiento.setTipoReferencia("transferencia_inventario");
        movimiento.setReferenciaId(transferenciaId);
        movimiento.setMotivo("Transferencia #" + transferenciaId);
        movimientoRepository.save(movimiento);
    }
}
