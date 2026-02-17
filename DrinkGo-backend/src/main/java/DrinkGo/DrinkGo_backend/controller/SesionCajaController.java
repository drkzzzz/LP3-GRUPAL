package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.SesionCajaDTO;
import DrinkGo.DrinkGo_backend.dto.AbrirSesionCajaRequest;
import DrinkGo.DrinkGo_backend.dto.CerrarSesionCajaRequest;
import DrinkGo.DrinkGo_backend.service.SesionCajaService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Controlador REST para Sesiones de Caja - Bloque 8
 * Gestión de apertura, cierre y consulta de sesiones de caja
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/sesiones")
public class SesionCajaController {

    @Autowired
    private SesionCajaService sesionService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * GET /api/sesiones - Listar todas las sesiones del negocio
     */
    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            Long negocioId = obtenerNegocioId();
            List<SesionCajaDTO> sesiones = sesionService.listar(negocioId);
            return ResponseEntity.ok(sesiones);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/sesiones/caja/{cajaId} - Listar sesiones por caja
     */
    @GetMapping("/caja/{cajaId}")
    public ResponseEntity<?> listarPorCaja(@PathVariable Long cajaId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<SesionCajaDTO> sesiones = sesionService.listarPorCaja(negocioId, cajaId);
            return ResponseEntity.ok(sesiones);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/sesiones/abiertas - Listar sesiones abiertas del negocio
     */
    @GetMapping("/abiertas")
    public ResponseEntity<?> listarAbiertas() {
        try {
            Long negocioId = obtenerNegocioId();
            List<SesionCajaDTO> sesiones = sesionService.listarAbiertas(negocioId);
            return ResponseEntity.ok(sesiones);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/sesiones/fecha/{fecha} - Listar sesiones por fecha
     */
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<?> listarPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            Long negocioId = obtenerNegocioId();
            List<SesionCajaDTO> sesiones = sesionService.listarPorFecha(negocioId, fecha);
            return ResponseEntity.ok(sesiones);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/sesiones/{id} - Obtener sesión por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            SesionCajaDTO sesion = sesionService.obtenerPorId(negocioId, id);
            return ResponseEntity.ok(sesion);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /api/sesiones/abrir - Abrir nueva sesión de caja
     */
    @PostMapping("/abrir")
    public ResponseEntity<?> abrirSesion(@RequestBody AbrirSesionCajaRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Long usuarioId = obtenerUsuarioId();

            SesionCajaDTO sesion = sesionService.abrirSesion(request, negocioId, usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Sesión de caja abierta exitosamente");
            response.put("sesion", sesion);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /api/sesiones/{id}/cerrar - Cerrar sesión de caja existente
     */
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<?> cerrarSesion(@PathVariable Long id, @RequestBody CerrarSesionCajaRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Long usuarioId = obtenerUsuarioId();

            SesionCajaDTO sesion = sesionService.cerrarSesion(id, request, negocioId, usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Sesión de caja cerrada exitosamente");
            response.put("sesion", sesion);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /api/sesiones/{id} - Eliminar sesión (solo si no está cerrada)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            sesionService.eliminar(id, negocioId);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Sesión eliminada exitosamente");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * Obtener negocioId desde contexto de autenticación JWT
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
     * Obtener usuarioId desde contexto de autenticación JWT
     */
    private Long obtenerUsuarioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerUsuarioId(uuid);
    }

    /**
     * Generar respuesta de error
     */
    private ResponseEntity<?> errorResponse(String mensaje) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
