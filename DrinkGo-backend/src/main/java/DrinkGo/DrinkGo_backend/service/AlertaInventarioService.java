package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.AlertaInventarioResponse;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para alertas de inventario (RF-INV-004..005).
 * Genera automáticamente alertas de:
 * - Stock bajo
 * - Próximo a vencer
 * - Vencido
 * - Sobrestock
 * - Punto de reorden
 */
@Service
public class AlertaInventarioService {

    private final AlertaInventarioRepository alertaRepository;
    private final StockInventarioRepository stockRepository;
    private final LoteInventarioRepository loteRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public AlertaInventarioService(AlertaInventarioRepository alertaRepository,
                                    StockInventarioRepository stockRepository,
                                    LoteInventarioRepository loteRepository,
                                    MovimientoInventarioRepository movimientoRepository) {
        this.alertaRepository = alertaRepository;
        this.stockRepository = stockRepository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /** Listar todas las alertas del negocio */
    public List<AlertaInventarioResponse> listar(Long negocioId) {
        return alertaRepository.findByNegocioIdOrderByCreadoEnDesc(negocioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /** Listar alertas activas (no resueltas) */
    public List<AlertaInventarioResponse> listarActivas(Long negocioId) {
        return alertaRepository.findByNegocioIdAndEstaResueltaFalseOrderByCreadoEnDesc(negocioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /** Obtener alerta por ID */
    public AlertaInventarioResponse obtenerPorId(Long id, Long negocioId) {
        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alerta de inventario", id));
        return convertirAResponse(alerta);
    }

    /** Resolver alerta (borrado lógico: esta_resuelta = true) */
    @Transactional
    public void resolver(Long id, Long negocioId, Long usuarioId) {
        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alerta de inventario", id));

        alerta.setEstaResuelta(true);
        alerta.setResueltaEn(LocalDateTime.now());
        alerta.setResueltaPor(usuarioId);
        alertaRepository.save(alerta);
    }

    /**
     * Verificar y generar alertas automáticas para un producto en un almacén.
     * Se ejecuta después de cada movimiento de inventario.
     */
    @Transactional
    public void verificarAlertas(Long negocioId, Producto producto, Almacen almacen) {
        // 1. Verificar stock bajo
        verificarStockBajo(negocioId, producto, almacen);

        // 2. Verificar sobrestock
        verificarSobrestock(negocioId, producto, almacen);

        // 3. Verificar punto de reorden
        verificarPuntoReorden(negocioId, producto, almacen);

        // 4. Verificar lotes próximos a vencer
        verificarProximoAVencer(negocioId, producto);

        // 5. Verificar lotes vencidos
        verificarVencidos(negocioId, producto);
    }

    // ── Verificaciones de alertas ──

    private void verificarStockBajo(Long negocioId, Producto producto, Almacen almacen) {
        if (producto.getStockMinimo() == null || producto.getStockMinimo() <= 0) return;

        Optional<StockInventario> stockOpt = stockRepository
                .findByProductoIdAndAlmacenIdAndNegocioId(producto.getId(), almacen.getId(), negocioId);

        if (stockOpt.isPresent()) {
            int stockActual = stockOpt.get().getCantidadEnMano();
            if (stockActual <= producto.getStockMinimo()) {
                crearAlertaSiNoExiste(negocioId, producto, almacen,
                        AlertaInventario.TipoAlerta.stock_bajo,
                        "Stock bajo para '" + producto.getNombre() + "' en almacén '" +
                        almacen.getNombre() + "'. Actual: " + stockActual +
                        ", Mínimo: " + producto.getStockMinimo(),
                        producto.getStockMinimo(), stockActual);
            }
        }
    }

    private void verificarSobrestock(Long negocioId, Producto producto, Almacen almacen) {
        if (producto.getStockMaximo() == null || producto.getStockMaximo() <= 0) return;

        Optional<StockInventario> stockOpt = stockRepository
                .findByProductoIdAndAlmacenIdAndNegocioId(producto.getId(), almacen.getId(), negocioId);

        if (stockOpt.isPresent()) {
            int stockActual = stockOpt.get().getCantidadEnMano();
            if (stockActual > producto.getStockMaximo()) {
                crearAlertaSiNoExiste(negocioId, producto, almacen,
                        AlertaInventario.TipoAlerta.sobrestock,
                        "Sobrestock para '" + producto.getNombre() + "' en almacén '" +
                        almacen.getNombre() + "'. Actual: " + stockActual +
                        ", Máximo: " + producto.getStockMaximo(),
                        producto.getStockMaximo(), stockActual);
            }
        }
    }

    private void verificarPuntoReorden(Long negocioId, Producto producto, Almacen almacen) {
        if (producto.getPuntoReorden() == null || producto.getPuntoReorden() <= 0) return;

        Optional<StockInventario> stockOpt = stockRepository
                .findByProductoIdAndAlmacenIdAndNegocioId(producto.getId(), almacen.getId(), negocioId);

        if (stockOpt.isPresent()) {
            int stockActual = stockOpt.get().getCantidadEnMano();
            if (stockActual <= producto.getPuntoReorden()) {
                crearAlertaSiNoExiste(negocioId, producto, almacen,
                        AlertaInventario.TipoAlerta.punto_reorden,
                        "Punto de reorden alcanzado para '" + producto.getNombre() +
                        "'. Actual: " + stockActual + ", Reorden: " + producto.getPuntoReorden(),
                        producto.getPuntoReorden(), stockActual);
            }
        }
    }

    private void verificarProximoAVencer(Long negocioId, Producto producto) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(30); // Alertar 30 días antes del vencimiento

        List<LoteInventario> lotesProximos = loteRepository
                .findByNegocioIdAndEstadoAndFechaVencimientoBetween(
                        negocioId, LoteInventario.EstadoLote.disponible, hoy, limite);

        for (LoteInventario lote : lotesProximos) {
            if (lote.getProductoId().equals(producto.getId())) {
                // No necesitamos el almacén para alertas de vencimiento
                crearAlertaSiNoExiste(negocioId, producto, null,
                        AlertaInventario.TipoAlerta.proximo_vencer,
                        "Lote '" + lote.getNumeroLote() + "' del producto '" +
                        producto.getNombre() + "' próximo a vencer el " +
                        lote.getFechaVencimiento() + ". Cantidad restante: " + lote.getCantidadRestante(),
                        null, lote.getCantidadRestante());
            }
        }
    }

    private void verificarVencidos(Long negocioId, Producto producto) {
        LocalDate hoy = LocalDate.now();

        List<LoteInventario> lotesVencidos = loteRepository
                .findByNegocioIdAndEstadoAndFechaVencimientoBefore(
                        negocioId, LoteInventario.EstadoLote.disponible, hoy);

        for (LoteInventario lote : lotesVencidos) {
            if (lote.getProductoId().equals(producto.getId())) {
                int cantidadRestante = lote.getCantidadRestante();

                // BUG-3 FIX: Descontar cantidadRestante del stock antes de marcar como vencido
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

                    // Registrar movimiento de vencimiento
                    Almacen almacenLote = lote.getAlmacen();
                    MovimientoInventario mov = new MovimientoInventario();
                    mov.setNegocioId(negocioId);
                    mov.setProducto(producto);
                    mov.setAlmacen(almacenLote);
                    mov.setLote(lote);
                    mov.setTipoMovimiento(MovimientoInventario.TipoMovimiento.vencimiento);
                    mov.setCantidad(cantidadRestante);
                    mov.setCostoUnitario(lote.getPrecioCompra());
                    mov.setTipoReferencia("lote_vencido");
                    mov.setReferenciaId(lote.getId());
                    mov.setMotivo("Lote '" + lote.getNumeroLote() + "' vencido el " + lote.getFechaVencimiento());
                    mov.setCreadoEn(LocalDateTime.now());
                    movimientoRepository.save(mov);
                }

                // Marcar lote como vencido y poner cantidad en 0
                lote.setEstado(LoteInventario.EstadoLote.vencido);
                lote.setCantidadRestante(0);
                loteRepository.save(lote);

                crearAlertaSiNoExiste(negocioId, producto, null,
                        AlertaInventario.TipoAlerta.vencido,
                        "Lote '" + lote.getNumeroLote() + "' del producto '" +
                        producto.getNombre() + "' VENCIDO desde " +
                        lote.getFechaVencimiento() + ". Cantidad descartada: " + cantidadRestante,
                        null, cantidadRestante);
            }
        }
    }

    private void crearAlertaSiNoExiste(Long negocioId, Producto producto, Almacen almacen,
                                        AlertaInventario.TipoAlerta tipo, String mensaje,
                                        Integer valorUmbral, Integer valorActual) {
        Long almacenId = almacen != null ? almacen.getId() : null;

        boolean yaExiste = alertaRepository
                .existsByNegocioIdAndProductoIdAndAlmacenIdAndTipoAlertaAndEstaResueltaFalse(
                        negocioId, producto.getId(), almacenId, tipo);

        if (!yaExiste) {
            AlertaInventario alerta = new AlertaInventario();
            alerta.setNegocioId(negocioId);
            alerta.setProducto(producto);
            alerta.setAlmacen(almacen);
            alerta.setTipoAlerta(tipo);
            alerta.setMensaje(mensaje);
            alerta.setValorUmbral(valorUmbral);
            alerta.setValorActual(valorActual);
            alerta.setEstaResuelta(false);
            alerta.setCreadoEn(LocalDateTime.now());
            alertaRepository.save(alerta);
        }
    }

    private AlertaInventarioResponse convertirAResponse(AlertaInventario alerta) {
        AlertaInventarioResponse resp = new AlertaInventarioResponse();
        resp.setId(alerta.getId());
        resp.setNegocioId(alerta.getNegocioId());
        resp.setProductoId(alerta.getProductoId());
        resp.setProductoNombre(alerta.getProducto() != null ? alerta.getProducto().getNombre() : null);
        resp.setAlmacenId(alerta.getAlmacenId());
        resp.setAlmacenNombre(alerta.getAlmacen() != null ? alerta.getAlmacen().getNombre() : null);
        resp.setTipoAlerta(alerta.getTipoAlerta() != null ? alerta.getTipoAlerta().name() : null);
        resp.setMensaje(alerta.getMensaje());
        resp.setValorUmbral(alerta.getValorUmbral());
        resp.setValorActual(alerta.getValorActual());
        resp.setEstaResuelta(alerta.getEstaResuelta());
        resp.setResueltaEn(alerta.getResueltaEn());
        resp.setResueltaPor(alerta.getResueltaPor());
        resp.setCreadoEn(alerta.getCreadoEn());
        return resp;
    }
}
