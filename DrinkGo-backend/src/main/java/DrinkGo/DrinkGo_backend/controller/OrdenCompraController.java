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

import DrinkGo.DrinkGo_backend.dto.OrdenCompraCreateRequest;
import DrinkGo.DrinkGo_backend.dto.OrdenCompraUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.OrdenCompra;
import DrinkGo.DrinkGo_backend.service.OrdenCompraService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;

/**
 * Controlador de Órdenes de Compra - Bloque 6 (Compras y Proveedores).
 * Todos los endpoints requieren JWT.
 * CRUD completo: GET, GET/{id}, POST, PUT, DELETE.
 * Implementa RF-COM-004 a RF-COM-007.
 */
@RestController
@RequestMapping("/restful/ordenes-compra")
public class OrdenCompraController {

    @Autowired
    private OrdenCompraService ordenCompraService;

    @Autowired
    private UsuarioService usuarioService;

    // ============================================================
    // CRUD ÓRDENES DE COMPRA
    // ============================================================

    /**
     * GET /restful/ordenes-compra
     * Listar todas las órdenes de compra del negocio autenticado.
     */
    @GetMapping
    public ResponseEntity<?> listarOrdenes() {
        try {
            Long negocioId = obtenerNegocioId();
            List<OrdenCompra> ordenes = ordenCompraService.listarOrdenes(negocioId);
            return ResponseEntity.ok(ordenes);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/ordenes-compra/{id}
     * Obtener una orden de compra por ID con sus detalles.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerOrden(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            Map<String, Object> orden = ordenCompraService.obtenerOrden(negocioId, id);
            return ResponseEntity.ok(orden);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/ordenes-compra
     * Crear una nueva orden de compra.
     */
    @PostMapping
    public ResponseEntity<?> crearOrden(@RequestBody OrdenCompraCreateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Long usuarioId = obtenerUsuarioId();
            Map<String, Object> resultado = ordenCompraService.crearOrden(negocioId, usuarioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Orden de compra creada exitosamente");
            response.putAll(resultado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /restful/ordenes-compra/{id}
     * Actualizar una orden de compra existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarOrden(@PathVariable Long id,
                                              @RequestBody OrdenCompraUpdateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Long usuarioId = obtenerUsuarioId();
            Map<String, Object> resultado = ordenCompraService.actualizarOrden(negocioId, id, usuarioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Orden de compra actualizada exitosamente");
            response.putAll(resultado);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /restful/ordenes-compra/{id}
     * Eliminar orden de compra (borrado lógico).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarOrden(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            ordenCompraService.eliminarOrden(negocioId, id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Orden de compra eliminada exitosamente (borrado lógico)");
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
