package DrinkGo.DrinkGo_backend.service.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Gastos;
import DrinkGo.DrinkGo_backend.entity.MovimientosCaja;
import DrinkGo.DrinkGo_backend.entity.SesionesCaja;
import DrinkGo.DrinkGo_backend.repository.GastosRepository;
import DrinkGo.DrinkGo_backend.repository.MovimientosCajaRepository;
import DrinkGo.DrinkGo_backend.repository.SesionesCajaRepository;
import DrinkGo.DrinkGo_backend.service.IGastosService;

@Service
public class GastosService implements IGastosService {
    @Autowired
    private GastosRepository repoGastos;

    @Autowired
    private SesionesCajaRepository repoSesionesCaja;

    @Autowired
    private MovimientosCajaRepository repoMovimientos;

    public List<Gastos> buscarTodos() {
        return repoGastos.findAll();
    }

    public List<Gastos> buscarPorNegocio(Long negocioId) {
        generarGastosPendientes(negocioId);
        return repoGastos.findByNegocioIdOrderByCreadoEnDesc(negocioId);
    }

    public Gastos guardar(Gastos gastos) {
        if (gastos.getNumeroGasto() == null || gastos.getNumeroGasto().isBlank()) {
            gastos.setNumeroGasto(generarNumeroGasto());
        }
        if (gastos.getEstado() == null) {
            gastos.setEstado(Gastos.EstadoGasto.pendiente);
        }
        // Si es recurrente, calcular la primera proximaEjecucion
        if (Boolean.TRUE.equals(gastos.getEsRecurrente()) && gastos.getProximaEjecucion() == null
                && gastos.getFechaGasto() != null && gastos.getPeriodoRecurrencia() != null) {
            gastos.setProximaEjecucion(
                    calcularSiguienteEjecucion(gastos.getFechaGasto(), gastos.getPeriodoRecurrencia()));
        }
        return repoGastos.save(gastos);
    }

    public Gastos modificar(Gastos gastos) {
        if (Boolean.TRUE.equals(gastos.getEsRecurrente()) && gastos.getProximaEjecucion() == null
                && gastos.getFechaGasto() != null && gastos.getPeriodoRecurrencia() != null) {
            gastos.setProximaEjecucion(
                    calcularSiguienteEjecucion(gastos.getFechaGasto(), gastos.getPeriodoRecurrencia()));
        }
        if (!Boolean.TRUE.equals(gastos.getEsRecurrente())) {
            gastos.setProximaEjecucion(null);
        }
        return repoGastos.save(gastos);
    }

    public Optional<Gastos> buscarId(Long id) {
        return repoGastos.findById(id);
    }

    public void eliminar(Long id) {
        repoGastos.deleteById(id);
    }

    /**
     * Marca un gasto como pagado y registra egreso en caja (si efectivo + caja
     * abierta).
     */
    public Gastos marcarPagado(Long id, String metodoPago, String referencia) {
        Gastos gasto = repoGastos.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado: " + id));
        return ejecutarPago(gasto, metodoPago, referencia);
    }

    /**
     * Lógica de pago reutilizable (manual y automático).
     */
    private Gastos ejecutarPago(Gastos gasto, String metodoPagoStr, String referencia) {
        gasto.setEstado(Gastos.EstadoGasto.pagado);
        if (metodoPagoStr != null && !metodoPagoStr.isBlank()) {
            try {
                gasto.setMetodoPago(Gastos.MetodoPago.valueOf(metodoPagoStr));
            } catch (IllegalArgumentException e) {
                gasto.setMetodoPago(Gastos.MetodoPago.otro);
            }
        }
        if (referencia != null && !referencia.isBlank()) {
            gasto.setReferenciaPago(referencia);
        }
        Gastos saved = repoGastos.save(gasto);

        // Registrar egreso en caja solo si efectivo
        boolean esEfectivo = saved.getMetodoPago() == Gastos.MetodoPago.efectivo;
        try {
            Long negocioId = gasto.getNegocio() != null ? gasto.getNegocio().getId() : null;
            if (negocioId != null && esEfectivo) {
                Optional<SesionesCaja> sesionOpt = repoSesionesCaja
                        .findByCajaNegocioIdOrderByFechaAperturaDesc(negocioId)
                        .stream()
                        .filter(s -> s.getEstadoSesion() == SesionesCaja.EstadoSesion.abierta)
                        .findFirst();
                if (sesionOpt.isPresent()) {
                    MovimientosCaja mov = new MovimientosCaja();
                    mov.setSesionCaja(sesionOpt.get());
                    mov.setTipoMovimiento(MovimientosCaja.TipoMovimiento.egreso_gasto);
                    mov.setMonto(gasto.getTotal() != null ? gasto.getTotal() : gasto.getMonto());
                    mov.setDescripcion("Pago gasto: " + gasto.getDescripcion());
                    mov.setGastoId(gasto.getId());
                    mov.setFechaMovimiento(LocalDateTime.now());
                    repoMovimientos.save(mov);
                }
            }
        } catch (Exception ignored) {
            // No bloquear el pago si hay error al registrar movimiento
        }
        return saved;
    }

    /* ─────────────────────────────────────────────────────────────────── */
    /*  SCHEDULER: Ejecuta pagos automáticos cada minuto                  */
    /* ─────────────────────────────────────────────────────────────────── */

    /**
     * Cada minuto revisa:
     * 1) Gastos recurrentes cuya proximaEjecucion ya venció → genera copia
     * pendiente.
     * 2) Gastos pendientes (no recurrentes) cuya fecha+hora ya pasó → los paga
     * automáticamente.
     */
    @Scheduled(fixedRate = 60_000) // cada 60 segundos
    public void procesarGastosProgramados() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        // 1) Generar copias de recurrentes vencidos (todos los negocios)
        List<Gastos> recurrentes = repoGastos
                .findByEsRecurrenteTrueAndProximaEjecucionLessThanEqual(hoy);
        for (Gastos template : recurrentes) {
            LocalDate prox = template.getProximaEjecucion();
            int generados = 0;
            while (prox != null && !prox.isAfter(hoy) && generados < 12) {
                Gastos copia = new Gastos();
                copia.setNegocio(template.getNegocio());
                copia.setSede(template.getSede());
                copia.setNumeroGasto(generarNumeroGasto());
                copia.setProveedor(template.getProveedor());
                copia.setDescripcion(template.getDescripcion());
                copia.setMonto(template.getMonto());
                copia.setMontoImpuesto(
                        template.getMontoImpuesto() != null ? template.getMontoImpuesto() : BigDecimal.ZERO);
                copia.setTotal(template.getTotal() != null ? template.getTotal() : template.getMonto());
                copia.setMoneda(template.getMoneda());
                copia.setFechaGasto(prox);
                copia.setHoraGasto(template.getHoraGasto());
                copia.setMetodoPago(template.getMetodoPago());
                copia.setEstado(Gastos.EstadoGasto.pendiente);
                copia.setEsRecurrente(false);
                copia.setNotas("Generado automáticamente desde gasto recurrente #" + template.getNumeroGasto());
                repoGastos.save(copia);

                prox = calcularSiguienteEjecucion(prox, template.getPeriodoRecurrencia());
                generados++;
            }
            template.setProximaEjecucion(prox);
            repoGastos.save(template);
        }

        // 2) Auto-pagar gastos pendientes cuya fecha+hora ya pasó
        List<Gastos> pendientes = repoGastos.findGastosPendientesDue(hoy);
        for (Gastos g : pendientes) {
            // Si tiene hora y aún no es la hora → skip
            if (g.getFechaGasto().isEqual(hoy) && g.getHoraGasto() != null
                    && ahora.isBefore(g.getHoraGasto())) {
                continue;
            }
            // La fecha ya pasó o (es hoy y la hora ya pasó/no tiene hora) → pagar
            try {
                ejecutarPago(g, g.getMetodoPago().name(), "Auto-pago programado");
            } catch (Exception ignored) {
                // Si falla un gasto, continuar con los demás
            }
        }
    }

    /**
     * Generar gastos pendientes por negocio (al listar).
     */
    public void generarGastosPendientes(Long negocioId) {
        LocalDate hoy = LocalDate.now();
        List<Gastos> recurrentes = repoGastos
                .findByNegocioIdAndEsRecurrenteTrueAndProximaEjecucionLessThanEqual(negocioId, hoy);

        for (Gastos template : recurrentes) {
            LocalDate prox = template.getProximaEjecucion();
            int generados = 0;
            while (prox != null && !prox.isAfter(hoy) && generados < 12) {
                Gastos copia = new Gastos();
                copia.setNegocio(template.getNegocio());
                copia.setSede(template.getSede());
                copia.setNumeroGasto(generarNumeroGasto());
                copia.setProveedor(template.getProveedor());
                copia.setDescripcion(template.getDescripcion());
                copia.setMonto(template.getMonto());
                copia.setMontoImpuesto(
                        template.getMontoImpuesto() != null ? template.getMontoImpuesto() : BigDecimal.ZERO);
                copia.setTotal(template.getTotal() != null ? template.getTotal() : template.getMonto());
                copia.setMoneda(template.getMoneda());
                copia.setFechaGasto(prox);
                copia.setHoraGasto(template.getHoraGasto());
                copia.setMetodoPago(template.getMetodoPago());
                copia.setEstado(Gastos.EstadoGasto.pendiente);
                copia.setEsRecurrente(false);
                copia.setNotas("Generado automáticamente desde gasto recurrente #" + template.getNumeroGasto());
                repoGastos.save(copia);

                prox = calcularSiguienteEjecucion(prox, template.getPeriodoRecurrencia());
                generados++;
            }
            template.setProximaEjecucion(prox);
            repoGastos.save(template);
        }
    }

    private LocalDate calcularSiguienteEjecucion(LocalDate desde, Gastos.PeriodoRecurrencia periodo) {
        if (desde == null || periodo == null)
            return null;
        return switch (periodo) {
            case semanal -> desde.plusWeeks(1);
            case quincenal -> desde.plusDays(15);
            case mensual -> desde.plusMonths(1);
            case trimestral -> desde.plusMonths(3);
            case anual -> desde.plusYears(1);
        };
    }

    private String generarNumeroGasto() {
        return "GAS-" + System.currentTimeMillis();
    }
}
