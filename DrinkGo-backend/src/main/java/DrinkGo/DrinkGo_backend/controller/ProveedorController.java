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

import DrinkGo.DrinkGo_backend.dto.ProveedorCreateRequest;
import DrinkGo.DrinkGo_backend.dto.ProveedorUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.Proveedor;
import DrinkGo.DrinkGo_backend.service.ProveedorService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;

/**
 * Controlador de Proveedores - Bloque 6.
 * Todos los endpoints requieren JWT.
 * CRUD completo: GET, POST, PUT, DELETE (borrado lógico).
 * Implementa RF-COM-001 a RF-COM-003.
 */
@RestController
@RequestMapping("/restful/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private UsuarioService usuarioService;

    // ============================================================
    // CRUD PROVEEDORES
    // ============================================================

    /**
     * GET /restful/proveedores
     * Listar todos los proveedores del negocio autenticado.
     */
    @GetMapping
    public ResponseEntity<?> listarProveedores() {
        try {
            Long negocioId = obtenerNegocioId();
            List<Proveedor> proveedores = proveedorService.listarProveedores(negocioId);
            return ResponseEntity.ok(proveedores);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/proveedores/{id}
     * Obtener un proveedor por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProveedor(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            Proveedor proveedor = proveedorService.obtenerProveedor(negocioId, id);
            return ResponseEntity.ok(proveedor);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/proveedores
     * Crear un nuevo proveedor.
     */
    @PostMapping
    public ResponseEntity<?> crearProveedor(@RequestBody ProveedorCreateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Proveedor proveedor = proveedorService.crearProveedor(negocioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Proveedor creado exitosamente");
            response.put("proveedor", proveedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /restful/proveedores/{id}
     * Actualizar un proveedor existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProveedor(@PathVariable Long id,
                                                  @RequestBody ProveedorUpdateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Proveedor proveedor = proveedorService.actualizarProveedor(negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Proveedor actualizado exitosamente");
            response.put("proveedor", proveedor);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /restful/proveedores/{id}
     * Eliminar proveedor (borrado lógico).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProveedor(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            proveedorService.eliminarProveedor(negocioId, id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Proveedor eliminado exitosamente (borrado lógico)");
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

    private ResponseEntity<?> errorResponse(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}