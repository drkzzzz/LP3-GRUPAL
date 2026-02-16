package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.AlertaCreateRequest;
import DrinkGo.DrinkGo_backend.dto.AlertaInventarioResponse;
import DrinkGo.DrinkGo_backend.dto.AlertaUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para alertas de inventario (RF-INV-004..005).
 * Detecta automáticamente stock bajo, productos próximos a vencer y vencidos.
 */
@Service
public class AlertaInventarioService {

    @Autowired
    private AlertaInventarioRepository alertaRepository;

    @Autowired
    private StockInventarioRepository stockRepository;

    @Autowired
    private LoteInventarioRepository loteRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    private static final int DIAS_ALERTA_VENCIMIENTO = 30;

    // ============================================================
    // CONSULTAS Y CRUD
    // ============================================================

    public List<AlertaInventarioResponse> listar(Long negocioId) {
        return alertaRepository.findByNegocioIdOrderByCreadoEnDesc(negocioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<AlertaInventarioResponse> listarActivas(Long negocioId) {
        return alertaRepository.findByNegocioIdAndEstaResueltaOrderByCreadoEnDesc(negocioId, false).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void resolver(Long id, Long negocioId, Long usuarioId) {
        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

        alerta.setEstaResuelta(true);
        alerta.setResueltaEn(LocalDateTime.now());
        alerta.setResueltaPor(usuarioId);
        alertaRepository.save(alerta);
    }

    @Transactional
    public void eliminarAlerta(Long negocioId, Long alertaId) {
        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(alertaId, negocioId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        alertaRepository.delete(alerta);
    }

    // ============================================================
    // LÓGICA DE VERIFICACIÓN AUTOMÁTICA
    // ============================================================

    /**
     * Verifica y genera alertas automáticas para un producto.
     * Se debe llamar después de cada entrada o salida de stock.
     */
    @Transactional
    public void verificarAlertas(Long negocioId, Producto producto, Almacen almacen) {
        verificarStockBajo(negocioId, producto, almacen);
        verificarProximoAVencer(negocioId, producto);
        verificarVencidos(negocioId, producto);
    }

    private void verificarStockBajo(Long negocioId, Producto producto, Almacen almacen) {
        if (producto.getStockMinimo() == null) return;

        Optional<StockInventario> stockOpt = stockRepository
                .findByNegocioIdAndProductoIdAndAlmacenId(negocioId, producto.getId(), almacen.getId());

        if (stockOpt.isPresent()) {
            int stockActual = stockOpt.get().getCantidadEnMano();
            if (stockActual <= producto.getStockMinimo()) {
                crearAlertaSiNoExiste(negocioId, producto, almacen,
                        AlertaInventario.TipoAlerta.stock_bajo,
                        "Stock bajo para '" + producto.getNombre() + "' en " + almacen.getNombre() + 
                        ". Actual: " + stockActual + ", Mínimo: " + producto.getStockMinimo(),
                        producto.getStockMinimo(), stockActual);
            }
        }
    }

    private void verificarProximoAVencer(Long negocioId, Producto producto) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(DIAS_ALERTA_VENCIMIENTO);

        List<LoteInventario> lotesProximos = loteRepository.findProximosAVencer(
                negocioId, LoteInventario.LoteEstado.disponible, hoy, limite);

        for (LoteInventario lote : lotesProximos) {
            if (lote.getProductoId().equals(producto.getId())) {
                long dias = ChronoUnit.DAYS.between(hoy, lote.getFechaVencimiento());
                crearAlertaSiNoExiste(negocioId, producto, lote.getAlmacen(),
                        AlertaInventario.TipoAlerta.proximo_vencer,
                        "Lote '" + lote.getNumeroLote() + "' vence en " + dias + " días.",
                        DIAS_ALERTA_VENCIMIENTO, (int) dias);
            }
        }
    }

    private void verificarVencidos(Long negocioId, Producto producto) {
        LocalDate hoy = LocalDate.now();
        List<LoteInventario> vencidos = loteRepository.findByNegocioIdAndEstadoAndFechaVencimientoBefore(
                negocioId, LoteInventario.LoteEstado.disponible, hoy);

        for (LoteInventario lote : vencidos) {
            if (lote.getProductoId().equals(producto.getId())) {
                crearAlertaSiNoExiste(negocioId, producto, lote.getAlmacen(),
                        AlertaInventario.TipoAlerta.vencido,
                        "¡Lote '" + lote.getNumeroLote() + "' VENCIDO!",
                        null, lote.getCantidadRestante());
            }
        }
    }

    private void crearAlertaSiNoExiste(Long negocioId, Producto producto, Almacen almacen,
                                      AlertaInventario.TipoAlerta tipo, String mensaje,
                                      Integer umbral, Integer actual) {
        Long almacenId = (almacen != null) ? almacen.getId() : null;
        boolean yaExiste = alertaRepository.existsByNegocioIdAndProductoIdAndAlmacenIdAndTipoAlertaAndEstaResuelta(
                negocioId, producto.getId(), almacenId, tipo, false);

        if (!yaExiste) {
            AlertaInventario alerta = new AlertaInventario();
            alerta.setNegocioId(negocioId);
            alerta.setProducto(producto);
            alerta.setAlmacen(almacen);
            alerta.setTipoAlerta(tipo);
            alerta.setMensaje(mensaje);
            alerta.setValorUmbral(umbral);
            alerta.setValorActual(actual);
            alerta.setEstaResuelta(false);
            alertaRepository.save(alerta);
        }
    }

    private AlertaInventarioResponse convertirAResponse(AlertaInventario alerta) {
        AlertaInventarioResponse resp = new AlertaInventarioResponse();
        resp.setId(alerta.getId());
        resp.setNegocioId(alerta.getNegocioId());
        resp.setProductoId(alerta.getProductoId());
        resp.setProductoNombre(alerta.getProducto() != null ? alerta.getProducto().getNombre() : "N/A");
        resp.setAlmacenId(alerta.getAlmacenId());
        resp.setAlmacenNombre(alerta.getAlmacen() != null ? alerta.getAlmacen().getNombre() : "N/A");
        resp.setTipoAlerta(alerta.getTipoAlerta().name());
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