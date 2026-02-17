package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.CajaRegistradoraDTO;
import DrinkGo.DrinkGo_backend.dto.CajaRegistradoraRequest;
import DrinkGo.DrinkGo_backend.service.CajaRegistradoraService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para Cajas Registradoras - Bloque 8
 * Gestión de cajas registradoras por sede
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/cajas")
public class CajaRegistradoraController {

    @Autowired
    private CajaRegistradoraService cajaService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * GET /api/cajas - Listar todas las cajas del negocio
     */
    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            Long negocioId = obtenerNegocioId();
            List<CajaRegistradoraDTO> cajas = cajaService.listar(negocioId);
            return ResponseEntity.ok(cajas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/cajas/sede/{sedeId} - Listar cajas por sede
     */
    @GetMapping("/sede/{sedeId}")
    public ResponseEntity<?> listarPorSede(@PathVariable Long sedeId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<CajaRegistradoraDTO> cajas = cajaService.listarPorSede(negocioId, sedeId);
            return ResponseEntity.ok(cajas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/cajas/activas - Listar solo cajas activas
     */
    @GetMapping("/activas")
    public ResponseEntity<?> listarActivas() {
        try {
            Long negocioId = obtenerNegocioId();
            List<CajaRegistradoraDTO> cajas = cajaService.listarActivas(negocioId);
            return ResponseEntity.ok(cajas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/cajas/{id} - Obtener una caja por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            CajaRegistradoraDTO caja = cajaService.obtenerPorId(negocioId, id);
            return ResponseEntity.ok(caja);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /api/cajas - Crear nueva caja registradora
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CajaRegistradoraRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            CajaRegistradoraDTO caja = cajaService.crear(negocioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Caja registradora creada exitosamente");
            response.put("caja", caja);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /api/cajas/{id} - Actualizar caja registradora
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody CajaRegistradoraRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            CajaRegistradoraDTO caja = cajaService.actualizar(negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Caja registradora actualizada exitosamente");
            response.put("caja", caja);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /api/cajas/{id} - Eliminar (soft delete) caja registradora
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            cajaService.eliminar(id, negocioId);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Caja registradora eliminada exitosamente");

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
     * Generar respuesta de error
     */
    private ResponseEntity<?> errorResponse(String mensaje) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
