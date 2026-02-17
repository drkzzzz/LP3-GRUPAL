package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.MovimientoCajaDTO;
import DrinkGo.DrinkGo_backend.dto.MovimientoCajaRequest;
import DrinkGo.DrinkGo_backend.service.MovimientoCajaService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para Movimientos de Caja - Bloque 8
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/movimientos-caja")
public class MovimientoCajaController {

    private final MovimientoCajaService movimientoService;
    private final UsuarioService usuarioService;

    public MovimientoCajaController(MovimientoCajaService movimientoService, UsuarioService usuarioService) {
        this.movimientoService = movimientoService;
        this.usuarioService = usuarioService;
    }

    /**
     * GET /api/movimientos-caja - Listar todos los movimientos del negocio
     */
    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            Long negocioId = obtenerNegocioId();
            List<MovimientoCajaDTO> movimientos = movimientoService.listar(negocioId);
            return ResponseEntity.ok(movimientos);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/movimientos-caja/sesion/{sesionId} - Listar movimientos de una
     * sesión
     */
    @GetMapping("/sesion/{sesionId}")
    public ResponseEntity<?> listarPorSesion(@PathVariable Long sesionId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<MovimientoCajaDTO> movimientos = movimientoService.listarPorSesion(negocioId, sesionId);
            return ResponseEntity.ok(movimientos);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/movimientos-caja/sesion/{sesionId}/tipo/{tipo} - Listar movimientos
     * por sesión y tipo
     */
    @GetMapping("/sesion/{sesionId}/tipo/{tipo}")
    public ResponseEntity<?> listarPorSesionYTipo(@PathVariable Long sesionId, @PathVariable String tipo) {
        try {
            Long negocioId = obtenerNegocioId();
            List<MovimientoCajaDTO> movimientos = movimientoService.listarPorSesionYTipo(negocioId, sesionId, tipo);
            return ResponseEntity.ok(movimientos);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/movimientos-caja/rango - Listar movimientos en rango de fechas
     */
    @GetMapping("/rango")
    public ResponseEntity<?> listarPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            Long negocioId = obtenerNegocioId();
            List<MovimientoCajaDTO> movimientos = movimientoService.listarPorRango(negocioId,
                    fechaInicio.atStartOfDay(), fechaFin.plusDays(1).atStartOfDay());
            return ResponseEntity.ok(movimientos);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/movimientos-caja/{id} - Obtener un movimiento por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            MovimientoCajaDTO movimiento = movimientoService.obtener(negocioId, id);
            return ResponseEntity.ok(movimiento);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /api/movimientos-caja - Crear nuevo movimiento de caja
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MovimientoCajaRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Long usuarioId = obtenerUsuarioId();

            MovimientoCajaDTO movimiento = movimientoService.crear(negocioId, usuarioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Movimiento de caja registrado exitosamente");
            response.put("movimiento", movimiento);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /api/movimientos-caja/{id} - Actualizar movimiento de caja
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody MovimientoCajaRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            MovimientoCajaDTO movimiento = movimientoService.actualizar(negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Movimiento de caja actualizado exitosamente");
            response.put("movimiento", movimiento);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /api/movimientos-caja/{id} - Eliminar (soft delete) movimiento de caja
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            movimientoService.eliminar(negocioId, id);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Movimiento de caja eliminado exitosamente");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * Extrae negocioId del contexto de seguridad JWT
     */
    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerNegocioId(uuid);
    }

    /**
     * Extrae usuarioId del contexto de seguridad JWT
     */
    private Long obtenerUsuarioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerUsuarioIdPorUuid(uuid);
    }

    /**
     * Construye respuesta de error
     */
    private ResponseEntity<?> errorResponse(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
