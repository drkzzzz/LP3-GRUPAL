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

import DrinkGo.DrinkGo_backend.dto.ProductoCreateRequest;
import DrinkGo.DrinkGo_backend.dto.ProductoUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.Producto;
import DrinkGo.DrinkGo_backend.service.ProductoService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;

/**
 * Controlador de Productos - Bloque 4 (Catálogo de Productos).
 * Todos los endpoints requieren JWT.
 * CRUD completo: GET, GET/{id}, POST, PUT, DELETE (borrado lógico).
 * Implementa RF-PRO-001 a RF-PRO-004.
 */
@RestController
@RequestMapping("/restful/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    // ============================================================
    // CRUD PRODUCTOS
    // ============================================================

    /**
     * GET /restful/productos
     * Listar todos los productos del negocio autenticado.
     */
    @GetMapping
    public ResponseEntity<?> listarProductos() {
        try {
            Long negocioId = obtenerNegocioId();
            List<Producto> productos = productoService.listarProductos(negocioId);
            return ResponseEntity.ok(productos);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/productos/{id}
     * Obtener un producto por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProducto(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            Producto producto = productoService.obtenerProducto(negocioId, id);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/productos
     * Crear un nuevo producto.
     */
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody ProductoCreateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Producto producto = productoService.crearProducto(negocioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Producto creado exitosamente");
            response.put("producto", producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /restful/productos/{id}
     * Actualizar un producto existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id,
                                                 @RequestBody ProductoUpdateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Producto producto = productoService.actualizarProducto(negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Producto actualizado exitosamente");
            response.put("producto", producto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /restful/productos/{id}
     * Eliminar producto (borrado lógico).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            productoService.eliminarProducto(negocioId, id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Producto eliminado exitosamente (borrado lógico)");
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
