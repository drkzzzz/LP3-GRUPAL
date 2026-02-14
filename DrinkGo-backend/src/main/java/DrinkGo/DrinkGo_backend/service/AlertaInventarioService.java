package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.AlertaCreateRequest;
import DrinkGo.DrinkGo_backend.dto.AlertaUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.AlertaInventario;
import DrinkGo.DrinkGo_backend.entity.AlertaInventario.TipoAlerta;
import DrinkGo.DrinkGo_backend.entity.LoteInventario;
import DrinkGo.DrinkGo_backend.entity.StockInventario;
import DrinkGo.DrinkGo_backend.repository.AlertaInventarioRepository;
import DrinkGo.DrinkGo_backend.repository.LoteInventarioRepository;
import DrinkGo.DrinkGo_backend.repository.StockInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de alertas de inventario - Bloque 5.
 * Implementa RF-INV-004 y RF-INV-005:
 * - Alertas de stock bajo
 * - Alertas de productos próximos a vencer
 * - Alertas de productos vencidos
 */
@Service
public class AlertaInventarioService {

    @Autowired
    private AlertaInventarioRepository alertaRepository;

    @Autowired
    private StockInventarioRepository stockRepository;

    @Autowired
    private LoteInventarioRepository loteRepository;

    // Días de anticipación para alertas de próximo a vencer
    private static final int DIAS_ALERTA_VENCIMIENTO = 30;

    /**
     * Listar todas las alertas del negocio.
     */
    public List<AlertaInventario> listarAlertas(Long negocioId) {
        return alertaRepository.findByNegocioIdOrderByCreadoEnDesc(negocioId);
    }

    /**
     * Listar alertas activas (no resueltas).
     */
    public List<AlertaInventario> listarAlertasActivas(Long negocioId) {
        return alertaRepository.findByNegocioIdAndEstaResueltaOrderByCreadoEnDesc(negocioId, false);
    }

    /**
     * Listar alertas por tipo.
     */
    public List<AlertaInventario> listarAlertasPorTipo(Long negocioId, TipoAlerta tipo) {
        return alertaRepository.findByNegocioIdAndTipoAlertaOrderByCreadoEnDesc(negocioId, tipo);
    }

    /**
     * Crear una alerta de inventario manualmente (POST).
     */
    @Transactional
    public AlertaInventario crearAlerta(Long negocioId, AlertaCreateRequest request) {
        if (request.getProductoId() == null) {
            throw new RuntimeException("El campo 'productoId' es obligatorio");
        }
        if (request.getTipoAlerta() == null || request.getTipoAlerta().isBlank()) {
            throw new RuntimeException("El campo 'tipoAlerta' es obligatorio");
        }
        if (request.getMensaje() == null || request.getMensaje().isBlank()) {
            throw new RuntimeException("El campo 'mensaje' es obligatorio");
        }

        TipoAlerta tipo;
        try {
            tipo = TipoAlerta.valueOf(request.getTipoAlerta());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de alerta inválido: " + request.getTipoAlerta() +
                    ". Valores válidos: stock_bajo, sobrestock, proximo_vencer, vencido, punto_reorden");
        }

        AlertaInventario alerta = new AlertaInventario();
        alerta.setNegocioId(negocioId);
        alerta.setProductoId(request.getProductoId());
        alerta.setAlmacenId(request.getAlmacenId());
        alerta.setTipoAlerta(tipo);
        alerta.setMensaje(request.getMensaje());
        alerta.setValorUmbral(request.getValorUmbral());
        alerta.setValorActual(request.getValorActual());
        alerta.setEstaResuelta(false);

        return alertaRepository.save(alerta);
    }

    /**
     * Resolver (marcar como resuelta) una alerta.
     */
    @Transactional
    public AlertaInventario resolverAlerta(Long negocioId, Long alertaId) {
        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(alertaId, negocioId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada: " + alertaId));

        if (alerta.getEstaResuelta()) {
            throw new RuntimeException("La alerta ya está resuelta");
        }

        alerta.setEstaResuelta(true);
        alerta.setResueltaEn(LocalDateTime.now());
        return alertaRepository.save(alerta);
    }

    /**
     * Actualizar una alerta de inventario.
     */
    @Transactional
    public AlertaInventario actualizarAlerta(Long negocioId, Long alertaId, AlertaUpdateRequest request) {
        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(alertaId, negocioId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada: " + alertaId));

        if (request.getMensaje() != null) {
            alerta.setMensaje(request.getMensaje());
        }
        if (request.getValorUmbral() != null) {
            alerta.setValorUmbral(request.getValorUmbral());
        }
        if (request.getValorActual() != null) {
            alerta.setValorActual(request.getValorActual());
        }
        if (request.getEstaResuelta() != null) {
            alerta.setEstaResuelta(request.getEstaResuelta());
            if (request.getEstaResuelta()) {
                alerta.setResueltaEn(LocalDateTime.now());
            }
        }

        return alertaRepository.save(alerta);
    }

    /**
     * Eliminar alerta (borrado lógico con @SQLDelete).
     */
    @Transactional
    public void eliminarAlerta(Long negocioId, Long alertaId) {
        AlertaInventario alerta = alertaRepository.findByIdAndNegocioId(alertaId, negocioId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada: " + alertaId));

        alertaRepository.delete(alerta);
    }

    /**
     * Verificar y generar alertas para un producto específico en un almacén.
     * Se llama automáticamente después de cada movimiento de inventario.
     */
    @Transactional
    public void verificarAlertasProducto(Long negocioId, Long productoId, Long almacenId) {
        verificarStockBajo(negocioId, productoId, almacenId);
        verificarLotesProximosAVencer(negocioId, productoId);
        verificarLotesVencidos(negocioId, productoId);
    }

    /**
     * Verificar y generar todas las alertas del negocio.
     * Se puede llamar periódicamente o bajo demanda.
     */
    @Transactional
    public void verificarTodasLasAlertas(Long negocioId) {
        // Stock bajo - usar query nativa
        List<StockInventario> stockBajo = stockRepository.findStockBajo(negocioId);
        for (StockInventario stock : stockBajo) {
            verificarStockBajo(negocioId, stock.getProductoId(), stock.getAlmacenId());
        }

        // Lotes próximos a vencer
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(DIAS_ALERTA_VENCIMIENTO);
        List<LoteInventario> lotesProximos = loteRepository.findLotesProximosAVencer(negocioId, hoy, fechaLimite);
        for (LoteInventario lote : lotesProximos) {
            crearAlertaSiNoExiste(negocioId, lote.getProductoId(), lote.getAlmacenId(),
                    TipoAlerta.proximo_vencer,
                    "Lote " + lote.getNumeroLote() + " próximo a vencer el " + lote.getFechaVencimiento(),
                    DIAS_ALERTA_VENCIMIENTO, (int) java.time.temporal.ChronoUnit.DAYS.between(hoy, lote.getFechaVencimiento()));
        }

        // Lotes vencidos
        List<LoteInventario> lotesVencidos = loteRepository.findLotesVencidos(negocioId, hoy);
        for (LoteInventario lote : lotesVencidos) {
            crearAlertaSiNoExiste(negocioId, lote.getProductoId(), lote.getAlmacenId(),
                    TipoAlerta.vencido,
                    "Lote " + lote.getNumeroLote() + " venció el " + lote.getFechaVencimiento() +
                            ". Cantidad restante: " + lote.getCantidadRestante(),
                    null, lote.getCantidadRestante());
        }
    }

    // ============================================================
    // MÉTODOS PRIVADOS DE VERIFICACIÓN
    // ============================================================

    private void verificarStockBajo(Long negocioId, Long productoId, Long almacenId) {
        StockInventario stock = stockRepository
                .findByNegocioIdAndProductoIdAndAlmacenId(negocioId, productoId, almacenId)
                .orElse(null);

        if (stock == null) return;

        // Obtener stock_minimo del producto mediante el stock en mano
        // Usamos la query nativa findStockBajo para verificar
        List<StockInventario> stocksBajos = stockRepository.findStockBajo(negocioId);
        boolean esBajo = stocksBajos.stream()
                .anyMatch(s -> s.getProductoId().equals(productoId) && s.getAlmacenId().equals(almacenId));

        if (esBajo) {
            crearAlertaSiNoExiste(negocioId, productoId, almacenId,
                    TipoAlerta.stock_bajo,
                    "Stock bajo para producto " + productoId +
                            " en almacén " + almacenId +
                            ". Cantidad actual: " + stock.getCantidadEnMano(),
                    null, stock.getCantidadEnMano());
        }
    }

    private void verificarLotesProximosAVencer(Long negocioId, Long productoId) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(DIAS_ALERTA_VENCIMIENTO);

        List<LoteInventario> lotesProximos = loteRepository.findLotesProximosAVencer(negocioId, hoy, fechaLimite);
        for (LoteInventario lote : lotesProximos) {
            if (lote.getProductoId().equals(productoId)) {
                long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(hoy, lote.getFechaVencimiento());
                crearAlertaSiNoExiste(negocioId, lote.getProductoId(), lote.getAlmacenId(),
                        TipoAlerta.proximo_vencer,
                        "Lote " + lote.getNumeroLote() + " vence en " + diasRestantes +
                                " días (" + lote.getFechaVencimiento() + ")",
                        DIAS_ALERTA_VENCIMIENTO, (int) diasRestantes);
            }
        }
    }

    private void verificarLotesVencidos(Long negocioId, Long productoId) {
        LocalDate hoy = LocalDate.now();

        List<LoteInventario> lotesVencidos = loteRepository.findLotesVencidos(negocioId, hoy);
        for (LoteInventario lote : lotesVencidos) {
            if (lote.getProductoId().equals(productoId)) {
                crearAlertaSiNoExiste(negocioId, lote.getProductoId(), lote.getAlmacenId(),
                        TipoAlerta.vencido,
                        "Lote " + lote.getNumeroLote() + " VENCIDO el " + lote.getFechaVencimiento() +
                                ". Cantidad restante: " + lote.getCantidadRestante(),
                        null, lote.getCantidadRestante());
            }
        }
    }

    /**
     * Crea una alerta solo si no existe una alerta activa del mismo tipo
     * para el mismo producto y almacén.
     */
    private void crearAlertaSiNoExiste(Long negocioId, Long productoId, Long almacenId,
                                        TipoAlerta tipo, String mensaje,
                                        Integer valorUmbral, Integer valorActual) {
        boolean yaExiste = alertaRepository.existsByNegocioIdAndProductoIdAndAlmacenIdAndTipoAlertaAndEstaResuelta(
                negocioId, productoId, almacenId, tipo, false);

        if (!yaExiste) {
            AlertaInventario alerta = new AlertaInventario();
            alerta.setNegocioId(negocioId);
            alerta.setProductoId(productoId);
            alerta.setAlmacenId(almacenId);
            alerta.setTipoAlerta(tipo);
            alerta.setMensaje(mensaje);
            alerta.setValorUmbral(valorUmbral);
            alerta.setValorActual(valorActual);
            alerta.setEstaResuelta(false);
            alertaRepository.save(alerta);
        }
    }
}
