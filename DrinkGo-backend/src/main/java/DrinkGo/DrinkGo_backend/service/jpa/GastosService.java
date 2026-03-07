package DrinkGo.DrinkGo_backend.service.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import DrinkGo.DrinkGo_backend.entity.Gastos;
import DrinkGo.DrinkGo_backend.repository.GastosRepository;
import DrinkGo.DrinkGo_backend.service.IGastosService;

@Service
public class GastosService implements IGastosService {
    @Autowired
    private GastosRepository repoGastos;

    @Autowired
    private FileStorageService fileStorageService;

    public List<Gastos> buscarTodos() {
        return repoGastos.findAll();
    }

    public List<Gastos> buscarPorNegocio(Long negocioId) {
        return repoGastos.findByNegocioIdOrderByCreadoEnDesc(negocioId);
    }

    public Gastos guardar(Gastos gastos) {
        if (gastos.getNumeroGasto() == null || gastos.getNumeroGasto().isBlank()) {
            gastos.setNumeroGasto(generarNumeroGasto());
        }
        if (gastos.getEstado() == null) {
            gastos.setEstado(Gastos.EstadoGasto.pendiente);
        }
        // Si es recurrente, la primera proximaEjecucion es la propia fechaGasto
        // para que el scheduler genere la copia del primer cobro y luego avance.
        if (Boolean.TRUE.equals(gastos.getEsRecurrente()) && gastos.getProximaEjecucion() == null
                && gastos.getFechaGasto() != null && gastos.getPeriodoRecurrencia() != null) {
            gastos.setProximaEjecucion(gastos.getFechaGasto());
        }
        return repoGastos.save(gastos);
    }

    public Gastos modificar(Gastos gastos) {
        Gastos existente = repoGastos.findById(gastos.getId())
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado: " + gastos.getId()));

        // Campos siempre editables (no rompen lógica de recurrencia)
        if (gastos.getDescripcion() != null) {
            existente.setDescripcion(gastos.getDescripcion());
        }
        if (gastos.getMonto() != null) {
            existente.setMonto(gastos.getMonto());
            existente.setMontoImpuesto(
                    gastos.getMontoImpuesto() != null ? gastos.getMontoImpuesto() : BigDecimal.ZERO);
            existente.setTotal(gastos.getTotal() != null ? gastos.getTotal() : gastos.getMonto());
        }
        if (gastos.getMetodoPago() != null) {
            existente.setMetodoPago(gastos.getMetodoPago());
        }
        existente.setCategoriaGasto(gastos.getCategoriaGasto());

        // Campos de programación: solo editables si NO es recurrente.
        // Para recurrentes se ignoran fecha, hora, periodo y toggle de recurrencia
        // para no romper la cadena de pagos ya generada.
        if (!Boolean.TRUE.equals(existente.getEsRecurrente())) {
            if (gastos.getFechaGasto() != null) {
                existente.setFechaGasto(gastos.getFechaGasto());
            }
            existente.setHoraGasto(gastos.getHoraGasto());

            existente.setEsRecurrente(Boolean.TRUE.equals(gastos.getEsRecurrente()));
            if (Boolean.TRUE.equals(existente.getEsRecurrente())) {
                existente.setPeriodoRecurrencia(gastos.getPeriodoRecurrencia());
                existente.setProximaEjecucion(existente.getFechaGasto());
            } else {
                existente.setPeriodoRecurrencia(null);
                existente.setProximaEjecucion(null);
            }
        }

        return repoGastos.save(existente);
    }

    public Optional<Gastos> buscarId(Long id) {
        return repoGastos.findById(id);
    }

    public void eliminar(Long id) {
        repoGastos.deleteById(id);
    }

    /**
     * Marca un gasto como pagado. Si es recurrente, crea un registro de pago
     * en el historial y avanza la fecha; si no, lo paga directamente.
     */
    public Gastos marcarPagado(Long id, String metodoPago, String referencia) {
        Gastos gasto = repoGastos.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado: " + id));
        if (Boolean.TRUE.equals(gasto.getEsRecurrente())) {
            return pagarRecurrente(gasto, metodoPago, referencia);
        }
        return ejecutarPago(gasto, metodoPago, referencia);
    }

    /**
     * Crea un registro de pago en el historial y avanza la fecha del gasto recurrente.
     */
    private Gastos pagarRecurrente(Gastos template, String metodoPagoStr, String referencia) {
        Gastos historial = new Gastos();
        historial.setNegocio(template.getNegocio());
        historial.setSede(template.getSede());
        historial.setNumeroGasto(generarNumeroGasto());
        historial.setProveedor(template.getProveedor());
        historial.setDescripcion(template.getDescripcion());
        historial.setCategoriaGasto(template.getCategoriaGasto());
        historial.setMonto(template.getMonto());
        historial.setMontoImpuesto(
                template.getMontoImpuesto() != null ? template.getMontoImpuesto() : BigDecimal.ZERO);
        historial.setTotal(template.getTotal() != null ? template.getTotal() : template.getMonto());
        historial.setMoneda(template.getMoneda());
        historial.setFechaGasto(LocalDate.now());
        historial.setHoraGasto(LocalTime.now());
        historial.setEstado(Gastos.EstadoGasto.pagado);
        historial.setEsRecurrente(false);
        historial.setNotas("Pago de gasto recurrente #" + template.getNumeroGasto());

        if (metodoPagoStr != null && !metodoPagoStr.isBlank()) {
            try {
                historial.setMetodoPago(Gastos.MetodoPago.valueOf(metodoPagoStr));
            } catch (IllegalArgumentException e) {
                historial.setMetodoPago(Gastos.MetodoPago.otro);
            }
        } else {
            historial.setMetodoPago(template.getMetodoPago());
        }
        if (referencia != null && !referencia.isBlank()) {
            historial.setReferenciaPago(referencia);
        }

        Gastos registroPagado = repoGastos.save(historial);

        // Avanzar proximaEjecucion al siguiente periodo
        template.setProximaEjecucion(
                calcularSiguienteEjecucion(template.getProximaEjecucion(), template.getPeriodoRecurrencia()));
        repoGastos.save(template);

        return registroPagado;
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
        return repoGastos.save(gasto);
    }

    /* ─────────────────────────────────────────────────────────────────── */
    /*  SCHEDULER: Ejecuta pagos automáticos cada minuto                  */
    /* ─────────────────────────────────────────────────────────────────── */

    /**
     * Cada minuto revisa:
     * 1) Gastos recurrentes cuya proximaEjecucion ya venció → crea registro pagado
     *    en el historial y avanza la fecha al siguiente periodo.
     * 2) Gastos pendientes (no recurrentes) cuya fecha+hora ya pasó → los paga
     *    automáticamente.
     */
    @Scheduled(fixedRate = 60_000) // cada 60 segundos
    public void procesarGastosProgramados() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        // 1) Procesar recurrentes vencidos: crear registros pagados directamente
        List<Gastos> recurrentes = repoGastos
                .findByEsRecurrenteTrueAndProximaEjecucionLessThanEqual(hoy);
        for (Gastos template : recurrentes) {
            int generados = 0;
            while (template.getProximaEjecucion() != null
                    && !template.getProximaEjecucion().isAfter(hoy)
                    && generados < 12) {
                LocalDate fechaPago = template.getProximaEjecucion();

                // Si es hoy y aún no es la hora configurada → esperar
                if (fechaPago.isEqual(hoy) && template.getHoraGasto() != null
                        && ahora.isBefore(template.getHoraGasto())) {
                    break;
                }

                // Crear registro pagado en el historial
                Gastos historial = new Gastos();
                historial.setNegocio(template.getNegocio());
                historial.setSede(template.getSede());
                historial.setNumeroGasto(generarNumeroGasto());
                historial.setProveedor(template.getProveedor());
                historial.setDescripcion(template.getDescripcion());
                historial.setCategoriaGasto(template.getCategoriaGasto());
                historial.setMonto(template.getMonto());
                historial.setMontoImpuesto(
                        template.getMontoImpuesto() != null ? template.getMontoImpuesto() : BigDecimal.ZERO);
                historial.setTotal(template.getTotal() != null ? template.getTotal() : template.getMonto());
                historial.setMoneda(template.getMoneda());
                historial.setFechaGasto(fechaPago);
                historial.setHoraGasto(template.getHoraGasto());
                historial.setMetodoPago(template.getMetodoPago());
                historial.setEstado(Gastos.EstadoGasto.pagado);
                historial.setEsRecurrente(false);
                historial.setReferenciaPago("Auto-pago programado");
                historial.setNotas("Generado automáticamente desde gasto recurrente #" + template.getNumeroGasto());
                repoGastos.save(historial);

                // Avanzar al siguiente periodo
                template.setProximaEjecucion(
                        calcularSiguienteEjecucion(fechaPago, template.getPeriodoRecurrencia()));
                generados++;
            }
            repoGastos.save(template);
        }

        // 2) Auto-pagar gastos pendientes (no recurrentes) cuya fecha+hora ya pasó
        List<Gastos> pendientes = repoGastos.findGastosPendientesDue(hoy);
        for (Gastos g : pendientes) {
            if (g.getFechaGasto().isEqual(hoy) && g.getHoraGasto() != null
                    && ahora.isBefore(g.getHoraGasto())) {
                continue;
            }
            try {
                ejecutarPago(g, g.getMetodoPago() != null ? g.getMetodoPago().name() : null, "Auto-pago programado");
            } catch (Exception ignored) {
            }
        }
    }

    private LocalDate calcularSiguienteEjecucion(LocalDate desde, Gastos.PeriodoRecurrencia periodo) {
        if (desde == null || periodo == null)
            return null;
        return switch (periodo) {
            case diario -> desde.plusDays(1);
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

    /**
     * Sube un comprobante (imagen/PDF) para un gasto pagado.
     */
    public Gastos subirComprobante(Long id, MultipartFile archivo) {
        Gastos gasto = repoGastos.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado: " + id));
        try {
            // Si ya tenía comprobante, eliminar el anterior
            if (gasto.getUrlComprobante() != null && !gasto.getUrlComprobante().isBlank()) {
                fileStorageService.eliminar(gasto.getUrlComprobante());
            }
            String ruta = fileStorageService.guardar(archivo, "comprobantes");
            gasto.setUrlComprobante(ruta);
            return repoGastos.save(gasto);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error al guardar el comprobante: " + e.getMessage());
        }
    }

    /**
     * Elimina el comprobante de un gasto.
     */
    public Gastos eliminarComprobante(Long id) {
        Gastos gasto = repoGastos.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado: " + id));
        if (gasto.getUrlComprobante() != null && !gasto.getUrlComprobante().isBlank()) {
            try {
                fileStorageService.eliminar(gasto.getUrlComprobante());
            } catch (java.io.IOException ignored) {
                // Si no se puede eliminar el archivo, continuar
            }
            gasto.setUrlComprobante(null);
            return repoGastos.save(gasto);
        }
        return gasto;
    }
}
