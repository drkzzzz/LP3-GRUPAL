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
        // Si es recurrente, la primera proximaEjecucion es la propia fechaGasto
        // para que el scheduler genere la copia del primer cobro y luego avance.
        if (Boolean.TRUE.equals(gastos.getEsRecurrente()) && gastos.getProximaEjecucion() == null
                && gastos.getFechaGasto() != null && gastos.getPeriodoRecurrencia() != null) {
            gastos.setProximaEjecucion(gastos.getFechaGasto());
        }
        return repoGastos.save(gastos);
    }

    public Gastos modificar(Gastos gastos) {
        if (Boolean.TRUE.equals(gastos.getEsRecurrente()) && gastos.getProximaEjecucion() == null
                && gastos.getFechaGasto() != null && gastos.getPeriodoRecurrencia() != null) {
            gastos.setProximaEjecucion(gastos.getFechaGasto());
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
        return repoGastos.save(gasto);
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
                copia.setCategoriaGasto(template.getCategoriaGasto());
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
                copia.setCategoriaGasto(template.getCategoriaGasto());
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
