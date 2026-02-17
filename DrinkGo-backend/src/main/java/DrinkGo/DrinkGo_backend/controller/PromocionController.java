package DrinkGo.DrinkGo_backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import DrinkGo.DrinkGo_backend.dto.PromocionCreateRequest;
import DrinkGo.DrinkGo_backend.dto.PromocionUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.Promocion;
import DrinkGo.DrinkGo_backend.service.PromocionService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;

/**
 * Controlador de Promociones y Descuentos - Bloque 13.
 * Todos los endpoints requieren JWT.
 * CRUD completo: GET, POST, PUT, DELETE (borrado lógico).
 * Incluye endpoint para listar solo las vigentes.
 * Implementa RF-PRO-015.
 */
@RestController
@RequestMapping("/restful/promociones")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @Autowired
    private UsuarioService usuarioService;

    // ============================================================
    // CRUD PROMOCIONES
    // ============================================================

    /**
     * GET /restful/promociones
     * Listar todas las promociones del negocio autenticado.
     */
    @GetMapping
    public ResponseEntity<?> listarPromociones() {
        try {
            Long negocioId = obtenerNegocioId();
            List<Promocion> promociones = promocionService.listarPromociones(negocioId);
            return ResponseEntity.ok(promociones);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/promociones/vigentes
     * Listar solo las promociones vigentes (activas y dentro de rango de fechas).
     */
    @GetMapping("/vigentes")
    public ResponseEntity<?> listarPromocionesVigentes() {
        try {
            Long negocioId = obtenerNegocioId();
            List<Promocion> promociones = promocionService.listarPromocionesVigentes(negocioId);
            return ResponseEntity.ok(promociones);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/promociones/{id}
     * Obtener una promoción por ID con sus condiciones y sedes.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPromocion(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            Map<String, Object> resultado = promocionService.obtenerPromocion(negocioId, id);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/promociones
     * Crear una nueva promoción con condiciones y sedes.
     */
    @PostMapping
    public ResponseEntity<?> crearPromocion(@RequestBody PromocionCreateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Long usuarioId = obtenerUsuarioId();
            Map<String, Object> resultado = promocionService.crearPromocion(negocioId, usuarioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Promoción creada exitosamente");
            response.put("promocion", resultado.get("promocion"));
            response.put("condiciones", resultado.get("condiciones"));
            response.put("sedes", resultado.get("sedes"));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /restful/promociones/{id}
     * Actualizar una promoción existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPromocion(@PathVariable Long id,
                                                  @RequestBody PromocionUpdateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Map<String, Object> resultado = promocionService.actualizarPromocion(negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Promoción actualizada exitosamente");
            response.put("promocion", resultado.get("promocion"));
            response.put("condiciones", resultado.get("condiciones"));
            response.put("sedes", resultado.get("sedes"));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /restful/promociones/{id}
     * Eliminar promoción (borrado lógico).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPromocion(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            promocionService.eliminarPromocion(negocioId, id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Promoción eliminada exitosamente (borrado lógico)");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No se pudo obtener la autenticación del contexto de seguridad");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerNegocioId(uuid);
    }

    private Long obtenerUsuarioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No se pudo obtener la autenticación del contexto de seguridad");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerUsuarioId(uuid);
    }

    private ResponseEntity<?> errorResponse(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}