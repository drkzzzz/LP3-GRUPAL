package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.TransferenciaDespachoRequest;
import DrinkGo.DrinkGo_backend.dto.TransferenciaRecepcionRequest;
import DrinkGo.DrinkGo_backend.dto.TransferenciaRequest;
import DrinkGo.DrinkGo_backend.dto.TransferenciaUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.TransferenciaInventario;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import DrinkGo.DrinkGo_backend.service.TransferenciaService;
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
 * Controlador de Transferencias de Inventario - Bloque 5.
 * Todos los endpoints requieren JWT.
 * CRUD completo: GET, POST, PUT, DELETE (borrado lógico).
 * Implementa RF-INV-007: Transferencias entre almacenes.
 * Flujo: borrador → pendiente → en_tránsito → recibida.
 */
@RestController
@RequestMapping("/restful/inventario/transferencia")
public class TransferenciaController {

    @Autowired
    private TransferenciaService transferenciaService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * GET /restful/inventario/transferencia
     * Listar todas las transferencias del negocio.
     */
    @GetMapping
    public ResponseEntity<?> listarTransferencias() {
        try {
            Long negocioId = obtenerNegocioId();
            List<TransferenciaInventario> transferencias = transferenciaService.listarTransferencias(negocioId);
            return ResponseEntity.ok(transferencias);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario/transferencia/{id}
     * Obtener una transferencia por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTransferencia(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            TransferenciaInventario transferencia = transferenciaService.obtenerTransferencia(negocioId, id);
            return ResponseEntity.ok(transferencia);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/inventario/transferencia
     * Crear una nueva transferencia (estado: borrador).
     */
    @PostMapping
    public ResponseEntity<?> crearTransferencia(@RequestBody TransferenciaRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            TransferenciaInventario transferencia = transferenciaService.crearTransferencia(negocioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Transferencia creada exitosamente");
            response.put("transferencia", transferencia);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/inventario/transferencia/{id}/despachar
     * Despachar una transferencia: descuenta stock de origen, cambia a en_transito.
     */
    @PostMapping("/{id}/despachar")
    public ResponseEntity<?> despacharTransferencia(
            @PathVariable Long id,
            @RequestBody(required = false) TransferenciaDespachoRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            TransferenciaInventario transferencia = transferenciaService.despacharTransferencia(
                    negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Transferencia despachada exitosamente");
            response.put("transferencia", transferencia);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/inventario/transferencia/{id}/recibir
     * Recibir una transferencia: ingresa stock a destino, cambia a recibida.
     */
    @PostMapping("/{id}/recibir")
    public ResponseEntity<?> recibirTransferencia(
            @PathVariable Long id,
            @RequestBody(required = false) TransferenciaRecepcionRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            TransferenciaInventario transferencia = transferenciaService.recibirTransferencia(
                    negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Transferencia recibida exitosamente");
            response.put("transferencia", transferencia);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/inventario/transferencia/{id}/cancelar
     * Cancelar una transferencia (solo si está en borrador o pendiente).
     */
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarTransferencia(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            TransferenciaInventario transferencia = transferenciaService.cancelarTransferencia(negocioId, id);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Transferencia cancelada exitosamente");
            response.put("transferencia", transferencia);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /restful/inventario/transferencia/{id}
     * Actualizar una transferencia (solo en estado 'borrador').
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTransferencia(
            @PathVariable Long id, @RequestBody TransferenciaUpdateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            TransferenciaInventario transferencia = transferenciaService.actualizarTransferencia(
                    negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Transferencia actualizada exitosamente");
            response.put("transferencia", transferencia);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /restful/inventario/transferencia/{id}
     * Eliminar transferencia (borrado lógico).
     * Solo si está en estado 'borrador' o 'cancelada'.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTransferencia(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            transferenciaService.eliminarTransferencia(negocioId, id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Transferencia eliminada exitosamente (borrado lógico)");
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
