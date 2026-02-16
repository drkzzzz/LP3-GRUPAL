package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.PlanSuscripcionCreateRequest;
import DrinkGo.DrinkGo_backend.dto.PlanSuscripcionUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.PlanSuscripcion;
import DrinkGo.DrinkGo_backend.repository.PlanSuscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de gestión de planes de suscripción - Bloque 1.
 * Implementa RF-PLT-002 a RF-PLT-005.
 */
@Service
public class PlanSuscripcionService {

    @Autowired
    private PlanSuscripcionRepository planRepository;

    /**
     * Crear un nuevo plan de suscripción (RF-PLT-002).
     */
    @Transactional
    public PlanSuscripcion crearPlan(PlanSuscripcionCreateRequest request) {
        // Validaciones
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del plan es obligatorio");
        }
        if (request.getSlug() == null || request.getSlug().isBlank()) {
            throw new RuntimeException("El slug del plan es obligatorio");
        }
        if (request.getPrecio() == null) {
            throw new RuntimeException("El precio del plan es obligatorio");
        }

        // Verificar slug único
        if (planRepository.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Ya existe un plan con el slug: " + request.getSlug());
        }

        // Crear plan
        PlanSuscripcion plan = new PlanSuscripcion();
        plan.setNombre(request.getNombre());
        plan.setSlug(request.getSlug());
        plan.setDescripcion(request.getDescripcion());
        plan.setPrecio(request.getPrecio());
        plan.setMoneda(request.getMoneda() != null ? request.getMoneda() : "PEN");
        
        if (request.getPeriodoFacturacion() != null) {
            plan.setPeriodoFacturacion(
                PlanSuscripcion.PeriodoFacturacion.valueOf(request.getPeriodoFacturacion())
            );
        }

        plan.setMaxSedes(request.getMaxSedes() != null ? request.getMaxSedes() : 1);
        plan.setMaxUsuarios(request.getMaxUsuarios() != null ? request.getMaxUsuarios() : 5);
        plan.setMaxProductos(request.getMaxProductos() != null ? request.getMaxProductos() : 500);
        plan.setMaxAlmacenesPorSede(request.getMaxAlmacenesPorSede() != null ? request.getMaxAlmacenesPorSede() : 2);
        
        plan.setPermitePos(request.getPermitePos() != null ? request.getPermitePos() : true);
        plan.setPermiteTiendaOnline(request.getPermiteTiendaOnline() != null ? request.getPermiteTiendaOnline() : false);
        plan.setPermiteDelivery(request.getPermiteDelivery() != null ? request.getPermiteDelivery() : false);
        plan.setPermiteMesas(request.getPermiteMesas() != null ? request.getPermiteMesas() : false);
        plan.setPermiteFacturacionElectronica(request.getPermiteFacturacionElectronica() != null ? request.getPermiteFacturacionElectronica() : false);
        plan.setPermiteMultiAlmacen(request.getPermiteMultiAlmacen() != null ? request.getPermiteMultiAlmacen() : false);
        plan.setPermiteReportesAvanzados(request.getPermiteReportesAvanzados() != null ? request.getPermiteReportesAvanzados() : false);
        plan.setPermiteAccesoApi(request.getPermiteAccesoApi() != null ? request.getPermiteAccesoApi() : false);
        
        plan.setFuncionalidadesJson(request.getFuncionalidadesJson());
        plan.setOrden(request.getOrden() != null ? request.getOrden() : 0);
        plan.setEstaActivo(true);

        return planRepository.save(plan);
    }

    /**
     * Listar todos los planes activos (RF-PLT-005).
     */
    public List<PlanSuscripcion> listarPlanesActivos() {
        return planRepository.findByEstaActivoOrderByOrdenAsc(true);
    }

    /**
     * Listar todos los planes (incluyendo inactivos).
     */
    public List<PlanSuscripcion> listarTodosLosPlanes() {
        return planRepository.findAll();
    }

    /**
     * Obtener plan por ID.
     */
    public PlanSuscripcion obtenerPlanPorId(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado con ID: " + id));
    }

    /**
     * Actualizar plan existente (RF-PLT-003).
     * Actualiza incrementando la versión.
     */
    @Transactional
    public PlanSuscripcion actualizarPlan(Long id, PlanSuscripcionUpdateRequest request) {
        PlanSuscripcion plan = obtenerPlanPorId(id);

        // Actualizar solo campos proporcionados
        if (request.getNombre() != null) {
            plan.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            plan.setDescripcion(request.getDescripcion());
        }
        if (request.getPrecio() != null) {
            plan.setPrecio(request.getPrecio());
        }
        if (request.getMoneda() != null) {
            plan.setMoneda(request.getMoneda());
        }
        if (request.getMaxSedes() != null) {
            plan.setMaxSedes(request.getMaxSedes());
        }
        if (request.getMaxUsuarios() != null) {
            plan.setMaxUsuarios(request.getMaxUsuarios());
        }
        if (request.getMaxProductos() != null) {
            plan.setMaxProductos(request.getMaxProductos());
        }
        if (request.getMaxAlmacenesPorSede() != null) {
            plan.setMaxAlmacenesPorSede(request.getMaxAlmacenesPorSede());
        }
        if (request.getPermitePos() != null) {
            plan.setPermitePos(request.getPermitePos());
        }
        if (request.getPermiteTiendaOnline() != null) {
            plan.setPermiteTiendaOnline(request.getPermiteTiendaOnline());
        }
        if (request.getPermiteDelivery() != null) {
            plan.setPermiteDelivery(request.getPermiteDelivery());
        }
        if (request.getPermiteMesas() != null) {
            plan.setPermiteMesas(request.getPermiteMesas());
        }
        if (request.getPermiteFacturacionElectronica() != null) {
            plan.setPermiteFacturacionElectronica(request.getPermiteFacturacionElectronica());
        }
        if (request.getPermiteMultiAlmacen() != null) {
            plan.setPermiteMultiAlmacen(request.getPermiteMultiAlmacen());
        }
        if (request.getPermiteReportesAvanzados() != null) {
            plan.setPermiteReportesAvanzados(request.getPermiteReportesAvanzados());
        }
        if (request.getPermiteAccesoApi() != null) {
            plan.setPermiteAccesoApi(request.getPermiteAccesoApi());
        }
        if (request.getFuncionalidadesJson() != null) {
            plan.setFuncionalidadesJson(request.getFuncionalidadesJson());
        }
        if (request.getOrden() != null) {
            plan.setOrden(request.getOrden());
        }

        // Incrementar versión
        plan.setVersion(plan.getVersion() + 1);

        return planRepository.save(plan);
    }

    /**
     * Desactivar plan (RF-PLT-004).
     * No elimina, solo marca como inactivo.
     */
    @Transactional
    public void desactivarPlan(Long id) {
        PlanSuscripcion plan = obtenerPlanPorId(id);
        plan.setEstaActivo(false);
        planRepository.save(plan);
    }

    /**
     * Reactivar plan desactivado.
     */
    @Transactional
    public void reactivarPlan(Long id) {
        PlanSuscripcion plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado con ID: " + id));
        plan.setEstaActivo(true);
        planRepository.save(plan);
    }
}
