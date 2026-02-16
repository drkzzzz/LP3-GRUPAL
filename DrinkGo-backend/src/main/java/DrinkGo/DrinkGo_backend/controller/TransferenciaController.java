package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.TransferenciaInventarioRequest;
import DrinkGo.DrinkGo_backend.dto.TransferenciaInventarioResponse;
import DrinkGo.DrinkGo_backend.service.TransferenciaInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de Transferencias de Inventario - Bloque 5.
 * Unificado: delega a TransferenciaInventarioService.
 * Implementa RF-INV-007: Transferencias entre almacenes.
 * Flujo: borrador → pendiente → en_tránsito → recibida.
 * negocioId hardcoded = 1L (JWT eliminado para entorno de desarrollo).
 */
@RestController
@RequestMapping("/restful/inventario/transferencia")
public class TransferenciaController {

    private static final Long NEGOCIO_ID = 1L;
    private static final Long USUARIO_ID = 1L;

    @Autowired
    private TransferenciaInventarioService transferenciaService;

    /** GET /restful/inventario/transferencia — Listar todas las transferencias */
    @GetMapping
    public ResponseEntity<?> listarTransferencias() {
        try {
            List<TransferenciaInventarioResponse> transferencias = transferenciaService.listar(NEGOCIO_ID);
            return ResponseEntity.ok(transferencias);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/transferencia/{id} — Obtener transferencia por ID */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTransferencia(@PathVariable Long id) {
        try {
            TransferenciaInventarioResponse transferencia = transferenciaService.obtenerPorId(id, NEGOCIO_ID);
            return ResponseEntity.ok(transferencia);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** POST /restful/inventario/transferencia — Crear transferencia (estado: borrador) */
    @PostMapping
    public ResponseEntity<?> crearTransferencia(@RequestBody TransferenciaInventarioRequest request) {
        try {
            TransferenciaInventarioResponse transferencia = transferenciaService.crear(request, NEGOCIO_ID, USUARIO_ID);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Transferencia creada exitosamente");
            response.put("transferencia", transferencia);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** POST /restful/inventario/transferencia/{id}/aprobar — Aprobar (borrador → pendiente) */
    @PostMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarTransferencia(@PathVariable Long id) {
        try {
            TransferenciaInventarioResponse transferencia = transferenciaService.aprobar(id, NEGOCIO_ID, USUARIO_ID);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Transferencia aprobada exitosamente");
            response.put("transferencia", transferencia);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** POST /restful/inventario/transferencia/{id}/despachar — Despachar (FIFO en origen) */
    @PostMapping("/{id}/despachar")
    public ResponseEntity<?> despacharTransferencia(@PathVariable Long id) {
        try {
            TransferenciaInventarioResponse transferencia = transferenciaService.despachar(id, NEGOCIO_ID, USUARIO_ID);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Transferencia despachada exitosamente");
            response.put("transferencia", transferencia);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** POST /restful/inventario/transferencia/{id}/recibir — Recibir (entrada en destino) */
    @PostMapping("/{id}/recibir")
    public ResponseEntity<?> recibirTransferencia(@PathVariable Long id) {
        try {
            TransferenciaInventarioResponse transferencia = transferenciaService.recibir(id, NEGOCIO_ID, USUARIO_ID);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Transferencia recibida exitosamente");
            response.put("transferencia", transferencia);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** POST /restful/inventario/transferencia/{id}/cancelar — Cancelar transferencia */
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarTransferencia(@PathVariable Long id) {
        try {
            transferenciaService.cancelar(id, NEGOCIO_ID);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Transferencia cancelada exitosamente (borrado lógico: estado → cancelada)");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** PUT /restful/inventario/transferencia/{id} — Actualizar (solo en borrador) */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTransferencia(@PathVariable Long id,
                                                      @RequestBody TransferenciaInventarioRequest request) {
        try {
            TransferenciaInventarioResponse transferencia = transferenciaService.actualizar(id, request, NEGOCIO_ID);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Transferencia actualizada exitosamente");
            response.put("transferencia", transferencia);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** DELETE /restful/inventario/transferencia/{id} — Cancelar (borrado lógico: estado → cancelada) */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTransferencia(@PathVariable Long id) {
        try {
            transferenciaService.cancelar(id, NEGOCIO_ID);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Transferencia cancelada exitosamente (borrado lógico: estado → cancelada)");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // ============================================================
    // MÉTODO AUXILIAR
    // ============================================================

    private ResponseEntity<?> errorResponse(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
