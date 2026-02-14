package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.SuscripcionCreateRequest;
import DrinkGo.DrinkGo_backend.entity.Suscripcion;
import DrinkGo.DrinkGo_backend.service.SuscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller de Suscripciones - Bloque 1.
 * Gestiona las suscripciones de los negocios.
 */
@RestController
@RequestMapping("/restful/suscripciones")
@CrossOrigin(origins = "*")
public class SuscripcionController {

    @Autowired
    private SuscripcionService suscripcionService;

    /**
     * GET /restful/suscripciones/mi-suscripcion - Obtener suscripción activa del negocio actual.
     * Requiere JWT token (RF-PLT-006).
     */
    @GetMapping("/mi-suscripcion")
    public ResponseEntity<?> obtenerMiSuscripcion() {
        try {
            // Obtener negocioId del SecurityContext
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long negocioId = Long.parseLong(auth.getName());

            Suscripcion suscripcion = suscripcionService.obtenerSuscripcionActiva(negocioId);
            return ResponseEntity.ok(suscripcion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * GET /restful/suscripciones - Listar suscripciones del negocio actual.
     */
    @GetMapping
    public ResponseEntity<?> listarMisSuscripciones() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long negocioId = Long.parseLong(auth.getName());

            List<Suscripcion> suscripciones = suscripcionService.listarSuscripcionesPorNegocio(negocioId);
            return ResponseEntity.ok(suscripciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /**
     * POST /restful/suscripciones - Crear nueva suscripción (RF-PLT-006).
     * Normalmente se crea desde el registro, pero permite crear manualmente.
     */
    @PostMapping
    public ResponseEntity<?> crearSuscripcion(@RequestBody SuscripcionCreateRequest request) {
        try {
            Suscripcion nuevaSuscripcion = suscripcionService.crearSuscripcion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSuscripcion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * PUT /restful/suscripciones/cambiar-plan - Cambiar plan de suscripción (RF-PLT-006).
     */
    @PutMapping("/cambiar-plan")
    public ResponseEntity<?> cambiarPlan(@RequestBody Map<String, Long> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long negocioId = Long.parseLong(auth.getName());
            Long nuevoPlanId = body.get("planId");

            if (nuevoPlanId == null) {
                return ResponseEntity.badRequest().body("planId es requerido");
            }

            Suscripcion suscripcionActualizada = suscripcionService.cambiarPlan(negocioId, nuevoPlanId);
            return ResponseEntity.ok(suscripcionActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * PUT /restful/suscripciones/suspender - Suspender suscripción (RF-FAC-002).
     */
    @PutMapping("/suspender")
    public ResponseEntity<?> suspenderSuscripcion(@RequestBody Map<String, String> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long negocioId = Long.parseLong(auth.getName());
            String razon = body.get("razon");

            suscripcionService.suspenderSuscripcion(negocioId, razon);
            return ResponseEntity.ok("Suscripción suspendida correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * PUT /restful/suscripciones/cancelar - Cancelar suscripción (RF-FAC-002).
     */
    @PutMapping("/cancelar")
    public ResponseEntity<?> cancelarSuscripcion(@RequestBody Map<String, String> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long negocioId = Long.parseLong(auth.getName());
            String razon = body.get("razon");

            suscripcionService.cancelarSuscripcion(negocioId, razon);
            return ResponseEntity.ok("Suscripción cancelada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * PUT /restful/suscripciones/reactivar - Reactivar suscripción suspendida.
     */
    @PutMapping("/reactivar")
    public ResponseEntity<?> reactivarSuscripcion() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long negocioId = Long.parseLong(auth.getName());

            suscripcionService.reactivarSuscripcion(negocioId);
            return ResponseEntity.ok("Suscripción reactivada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * GET /restful/suscripciones/todas - Listar todas las suscripciones (SuperAdmin).
     */
    @GetMapping("/todas")
    public ResponseEntity<List<Suscripcion>> listarTodasLasSuscripciones() {
        List<Suscripcion> suscripciones = suscripcionService.listarTodasLasSuscripciones();
        return ResponseEntity.ok(suscripciones);
    }

    /**
     * GET /restful/suscripciones/por-estado/{estado} - Listar por estado (SuperAdmin).
     */
    @GetMapping("/por-estado/{estado}")
    public ResponseEntity<?> listarPorEstado(@PathVariable String estado) {
        try {
            List<Suscripcion> suscripciones = suscripcionService.listarPorEstado(estado);
            return ResponseEntity.ok(suscripciones);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Estado inválido. Valores permitidos: activa, suspendida, cancelada, pendiente_pago");
        }
    }
}
