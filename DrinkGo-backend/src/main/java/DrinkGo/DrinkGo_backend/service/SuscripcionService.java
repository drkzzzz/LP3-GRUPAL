package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.SuscripcionCreateRequest;
import DrinkGo.DrinkGo_backend.entity.PlanSuscripcion;
import DrinkGo.DrinkGo_backend.entity.Suscripcion;
import DrinkGo.DrinkGo_backend.repository.PlanSuscripcionRepository;
import DrinkGo.DrinkGo_backend.repository.SuscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de gestión de suscripciones - Bloque 1.
 * Implementa RF-PLT-006, RF-FAC-001, RF-FAC-002.
 */
@Service
public class SuscripcionService {

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private PlanSuscripcionRepository planRepository;

    /**
     * Crear suscripción para un negocio (RF-PLT-006).
     */
    @Transactional
    public Suscripcion crearSuscripcion(SuscripcionCreateRequest request) {
        // Validaciones
        if (request.getNegocioId() == null) {
            throw new RuntimeException("El negocioId es obligatorio");
        }
        if (request.getPlanId() == null) {
            throw new RuntimeException("El planId es obligatorio");
        }

        // Verificar que el plan existe y está activo
        PlanSuscripcion plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        
        if (!plan.getEstaActivo()) {
            throw new RuntimeException("El plan seleccionado no está activo");
        }

        // Verificar que el negocio no tenga ya una suscripción activa
        suscripcionRepository.findByNegocioIdAndEstado(
            request.getNegocioId(), 
            Suscripcion.EstadoSuscripcion.activa
        ).ifPresent(s -> {
            throw new RuntimeException("El negocio ya tiene una suscripción activa");
        });

        // Crear suscripción
        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setNegocioId(request.getNegocioId());
        suscripcion.setPlanId(request.getPlanId());
        suscripcion.setEstado(Suscripcion.EstadoSuscripcion.activa);
        
        // Fechas del período
        LocalDate inicio = request.getInicioPeriodoActual() != null ? 
            request.getInicioPeriodoActual() : LocalDate.now();
        suscripcion.setInicioPeriodoActual(inicio);

        // Calcular fin según período del plan
        LocalDate fin;
        if (plan.getPeriodoFacturacion() == PlanSuscripcion.PeriodoFacturacion.mensual) {
            fin = inicio.plusMonths(1);
        } else {
            fin = inicio.plusYears(1);
        }
        suscripcion.setFinPeriodoActual(request.getFinPeriodoActual() != null ? 
            request.getFinPeriodoActual() : fin);

        suscripcion.setProximaFechaFacturacion(fin);

        return suscripcionRepository.save(suscripcion);
    }

    /**
     * Obtener suscripción activa de un negocio.
     */
    public Suscripcion obtenerSuscripcionActiva(Long negocioId) {
        return suscripcionRepository.findByNegocioIdAndEstado(
            negocioId, 
            Suscripcion.EstadoSuscripcion.activa
        ).orElseThrow(() -> new RuntimeException("No se encontró suscripción activa para el negocio"));
    }

    /**
     * Listar todas las suscripciones de un negocio.
     */
    public List<Suscripcion> listarSuscripcionesPorNegocio(Long negocioId) {
        return suscripcionRepository.findByNegocioId(negocioId);
    }

    /**
     * Cambiar plan de suscripción (RF-PLT-006).
     */
    @Transactional
    public Suscripcion cambiarPlan(Long negocioId, Long nuevoPlanId) {
        // Obtener suscripción activa
        Suscripcion suscripcion = obtenerSuscripcionActiva(negocioId);

        // Verificar nuevo plan
        PlanSuscripcion nuevoPlan = planRepository.findById(nuevoPlanId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        
        if (!nuevoPlan.getEstaActivo()) {
            throw new RuntimeException("El plan seleccionado no está activo");
        }

        // Cambiar plan
        suscripcion.setPlanId(nuevoPlanId);

        return suscripcionRepository.save(suscripcion);
    }

    /**
     * Suspender suscripción (RF-FAC-002).
     */
    @Transactional
    public void suspenderSuscripcion(Long negocioId, String razon) {
        Suscripcion suscripcion = obtenerSuscripcionActiva(negocioId);
        suscripcion.setEstado(Suscripcion.EstadoSuscripcion.suspendida);
        suscripcion.setRazonCancelacion(razon);
        suscripcionRepository.save(suscripcion);
    }

    /**
     * Cancelar suscripción (RF-FAC-002).
     */
    @Transactional
    public void cancelarSuscripcion(Long negocioId, String razon) {
        Suscripcion suscripcion = obtenerSuscripcionActiva(negocioId);
        suscripcion.setEstado(Suscripcion.EstadoSuscripcion.cancelada);
        suscripcion.setCanceladaEn(LocalDateTime.now());
        suscripcion.setRazonCancelacion(razon);
        suscripcionRepository.save(suscripcion);
    }

    /**
     * Reactivar suscripción suspendida.
     */
    @Transactional
    public void reactivarSuscripcion(Long negocioId) {
        Suscripcion suscripcion = suscripcionRepository.findByNegocioIdAndEstado(
            negocioId, 
            Suscripcion.EstadoSuscripcion.suspendida
        ).orElseThrow(() -> new RuntimeException("No hay suscripción suspendida para reactivar"));

        suscripcion.setEstado(Suscripcion.EstadoSuscripcion.activa);
        suscripcion.setRazonCancelacion(null);
        suscripcionRepository.save(suscripcion);
    }

    /**
     * Listar todas las suscripciones (para SuperAdmin).
     */
    public List<Suscripcion> listarTodasLasSuscripciones() {
        return suscripcionRepository.findAll();
    }

    /**
     * Listar suscripciones por estado.
     */
    public List<Suscripcion> listarPorEstado(String estado) {
        Suscripcion.EstadoSuscripcion estadoEnum = Suscripcion.EstadoSuscripcion.valueOf(estado);
        return suscripcionRepository.findByEstado(estadoEnum);
    }
}
