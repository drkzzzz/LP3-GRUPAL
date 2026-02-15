package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.AprobarDevolucionRequest;
import DrinkGo.DrinkGo_backend.dto.CreateDevolucionRequest;
import DrinkGo.DrinkGo_backend.dto.DevolucionResponse;
import DrinkGo.DrinkGo_backend.dto.UpdateDevolucionRequest;
import DrinkGo.DrinkGo_backend.service.DevolucionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller de Devoluciones - Bloque 11.
 * Gestiona las devoluciones y reembolsos (RF-FIN-008..012).
 */
@RestController
@RequestMapping("/restful/devoluciones")
@CrossOrigin(origins = "*")
public class DevolucionController {

    @Autowired
    private DevolucionService devolucionService;

    /**
     * GET /restful/devoluciones - Listar todas las devoluciones del negocio.
     * RF-FIN-008: Consultar devoluciones registradas.
     */
    @GetMapping
    public ResponseEntity<?> listarDevoluciones() {
        try {
            // Obtener negocioId del SecurityContext (o usar mock temporal)
            Long negocioId = obtenerNegocioId();
            
            List<DevolucionResponse> devoluciones = devolucionService.obtenerTodas(negocioId);
            return ResponseEntity.ok(devoluciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /restful/devoluciones/{id} - Obtener una devolución por ID.
     * RF-FIN-008: Consultar detalle de devolución específica.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDevolucion(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            
            DevolucionResponse devolucion = devolucionService.obtenerPorId(id, negocioId);
            return ResponseEntity.ok(devolucion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /restful/devoluciones - Crear nueva devolución.
     * RF-FIN-008: Registrar Solicitudes de Devolución.
     */
    @PostMapping
    public ResponseEntity<?> crearDevolucion(@RequestBody CreateDevolucionRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            
            // Asignar negocioId del contexto
            if (request.getNegocioId() == null) {
                request.setNegocioId(negocioId);
            }
            
            DevolucionResponse nuevaDevolucion = devolucionService.crearDevolucion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaDevolucion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /restful/devoluciones/{id} - Actualizar devolución existente.
     * Solo se puede actualizar si está en estado 'solicitada'.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDevolucion(
            @PathVariable Long id,
            @RequestBody UpdateDevolucionRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            
            DevolucionResponse devolucionActualizada = 
                    devolucionService.actualizarDevolucion(id, request, negocioId);
            return ResponseEntity.ok(devolucionActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /restful/devoluciones/{id}/aprobar - Aprobar devolución.
     * RF-FIN-009: Gestionar Motivos y Aprobación de Devoluciones.
     */
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarDevolucion(
            @PathVariable Long id,
            @RequestBody AprobarDevolucionRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            
            DevolucionResponse devolucionAprobada = 
                    devolucionService.aprobarDevolucion(id, request, negocioId);
            return ResponseEntity.ok(devolucionAprobada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /restful/devoluciones/{id}/rechazar - Rechazar devolución.
     * RF-FIN-009: Gestionar Motivos y Aprobación de Devoluciones.
     */
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarDevolucion(
            @PathVariable Long id,
            @RequestBody AprobarDevolucionRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            
            DevolucionResponse devolucionRechazada = 
                    devolucionService.rechazarDevolucion(id, request, negocioId);
            return ResponseEntity.ok(devolucionRechazada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /restful/devoluciones/{id}/completar - Completar devolución y procesar reembolso.
     * RF-FIN-010: Generar Ajustes Financieros.
     * RF-FIN-011: Gestionar Reembolsos al Cliente.
     * RF-FIN-012: Reintegrar Productos Devueltos al Inventario.
     */
    @PutMapping("/{id}/completar")
    public ResponseEntity<?> completarDevolucion(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        try {
            Long negocioId = obtenerNegocioId();
            Long procesadoPor = body.getOrDefault("procesadoPor", 1L);
            
            DevolucionResponse devolucionCompletada = 
                    devolucionService.completarDevolucion(id, procesadoPor, negocioId);
            return ResponseEntity.ok(devolucionCompletada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /restful/devoluciones/{id} - Eliminar devolución.
     * Solo se puede eliminar si está en estado 'solicitada'.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDevolucion(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            
            String mensaje = devolucionService.eliminarDevolucion(id, negocioId);
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // --- Métodos auxiliares ---

    /**
     * Obtiene el negocioId del contexto de seguridad.
     * En MODO DESARROLLO: Usa negocioId = 1 si no hay autenticación.
     */
    private Long obtenerNegocioId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null && !auth.getName().equals("anonymousUser")) {
                return Long.parseLong(auth.getName());
            }
        } catch (Exception e) {
            // Ignorar errores de autenticación en modo desarrollo
        }
        
        // MODO DESARROLLO: Retornar negocioId por defecto
        return 1L;
    }
}
