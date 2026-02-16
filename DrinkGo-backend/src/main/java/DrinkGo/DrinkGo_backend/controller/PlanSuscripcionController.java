package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.PlanSuscripcionCreateRequest;
import DrinkGo.DrinkGo_backend.dto.PlanSuscripcionUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.PlanSuscripcion;
import DrinkGo.DrinkGo_backend.service.PlanSuscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de Planes de Suscripción - Bloque 1.
 * Endpoints públicos para consultar planes disponibles.
 */
@RestController
@RequestMapping("/restful/planes")
@CrossOrigin(origins = "*")
public class PlanSuscripcionController {

    @Autowired
    private PlanSuscripcionService planService;

    /**
     * GET /restful/planes - Listar planes activos (RF-PLT-005).
     * Endpoint público sin autenticación.
     */
    @GetMapping
    public ResponseEntity<List<PlanSuscripcion>> listarPlanesActivos() {
        List<PlanSuscripcion> planes = planService.listarPlanesActivos();
        return ResponseEntity.ok(planes);
    }

    /**
     * GET /restful/planes/todos - Listar todos los planes (SuperAdmin).
     */
    @GetMapping("/todos")
    public ResponseEntity<List<PlanSuscripcion>> listarTodosLosPlanes() {
        List<PlanSuscripcion> planes = planService.listarTodosLosPlanes();
        return ResponseEntity.ok(planes);
    }

    /**
     * GET /restful/planes/{id} - Obtener plan por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPlanPorId(@PathVariable Long id) {
        try {
            PlanSuscripcion plan = planService.obtenerPlanPorId(id);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * POST /restful/planes - Crear nuevo plan (RF-PLT-002) (SuperAdmin).
     */
    @PostMapping
    public ResponseEntity<?> crearPlan(@RequestBody PlanSuscripcionCreateRequest request) {
        try {
            PlanSuscripcion nuevoPlan = planService.crearPlan(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPlan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * PUT /restful/planes/{id} - Actualizar plan (RF-PLT-003) (SuperAdmin).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPlan(
            @PathVariable Long id,
            @RequestBody PlanSuscripcionUpdateRequest request) {
        try {
            PlanSuscripcion planActualizado = planService.actualizarPlan(id, request);
            return ResponseEntity.ok(planActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * DELETE /restful/planes/{id} - Desactivar plan (RF-PLT-004) (SuperAdmin).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivarPlan(@PathVariable Long id) {
        try {
            planService.desactivarPlan(id);
            return ResponseEntity.ok("Plan desactivado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * PUT /restful/planes/{id}/reactivar - Reactivar plan desactivado (SuperAdmin).
     */
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivarPlan(@PathVariable Long id) {
        try {
            planService.reactivarPlan(id);
            return ResponseEntity.ok("Plan reactivado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
