package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.OrdenCompraCreateRequest;
import DrinkGo.DrinkGo_backend.dto.OrdenCompraUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.OrdenCompra;
import DrinkGo.DrinkGo_backend.entity.Usuario;
import DrinkGo.DrinkGo_backend.service.OrdenCompraService;
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
 * Controlador de Compras (Órdenes de Compra) - Bloque 6.
 * Todos los endpoints requieren JWT.
 * CRUD completo: GET, POST, PUT, DELETE.
 * Implementa RF-COM-004 a RF-COM-007.
 */
@RestController
@RequestMapping("/restful/compras")
public class CompraController {

    @Autowired
    private OrdenCompraService ordenCompraService;

    @Autowired
    private UsuarioService usuarioService;

    // ============================================================
    // CRUD ÓRDENES DE COMPRA
    // ============================================================

    /**
     * GET /restful/compras
     * Listar todas las órdenes de compra del negocio autenticado.
     */
    @GetMapping
    public ResponseEntity<?> listarCompras() {
        try {
            Long negocioId = obtenerNegocioId();
            List<OrdenCompra> ordenes = ordenCompraService.listarOrdenes(negocioId);
            return ResponseEntity.ok(ordenes);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/compras/{id}
     * Obtener una orden de compra por ID con su detalle.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCompra(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            Map<String, Object> resultado = ordenCompraService.obtenerOrden(negocioId, id);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/compras
     * Crear una nueva orden de compra con sus items.
     */
    @PostMapping
    public ResponseEntity<?> crearCompra(@RequestBody OrdenCompraCreateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Long usuarioId = obtenerUsuarioId();
            Map<String, Object> resultado = ordenCompraService.crearOrden(negocioId, usuarioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Orden de compra creada exitosamente");
            response.put("orden", resultado.get("orden"));
            response.put("items", resultado.get("items"));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /restful/compras/{id}
     * Actualizar una orden de compra existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCompra(@PathVariable Long id,
                                               @RequestBody OrdenCompraUpdateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Long usuarioId = obtenerUsuarioId();
            Map<String, Object> resultado = ordenCompraService.actualizarOrden(negocioId, id, usuarioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Orden de compra actualizada exitosamente");
            response.put("orden", resultado.get("orden"));
            response.put("items", resultado.get("items"));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /restful/compras/{id}
     * Eliminar (cancelar) una orden de compra.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCompra(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            ordenCompraService.eliminarOrden(negocioId, id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Orden de compra eliminada exitosamente");
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