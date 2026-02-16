package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.AlertaCreateRequest;
import DrinkGo.DrinkGo_backend.dto.AlertaUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.AlertaInventario;
import DrinkGo.DrinkGo_backend.entity.AlertaInventario.TipoAlerta;
import DrinkGo.DrinkGo_backend.service.AlertaInventarioService;
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
 * Controlador de Alertas de Inventario - Bloque 5.
 * Todos los endpoints requieren JWT.
 * CRUD completo: GET, POST, PUT, DELETE (borrado lógico).
 * Implementa RF-INV-004 y RF-INV-005.
 */
@RestController
@RequestMapping("/restful/inventario/alertas")
public class AlertaController {

    @Autowired
    private AlertaInventarioService alertaService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * GET /restful/inventario/alertas
     * Listar todas las alertas activas del negocio.
     */
    @GetMapping
    public ResponseEntity<?> listarAlertasActivas() {
        try {
            Long negocioId = obtenerNegocioId();
            List<AlertaInventario> alertas = alertaService.listarAlertasActivas(negocioId);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario/alertas/todas
     * Listar todas las alertas (activas y resueltas).
     */
    @GetMapping("/todas")
    public ResponseEntity<?> listarTodasLasAlertas() {
        try {
            Long negocioId = obtenerNegocioId();
            List<AlertaInventario> alertas = alertaService.listarAlertas(negocioId);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario/alertas/tipo/{tipoAlerta}
     * Listar alertas por tipo (stock_bajo, proximo_vencer, vencido, etc.).
     */
    @GetMapping("/tipo/{tipoAlerta}")
    public ResponseEntity<?> listarAlertasPorTipo(@PathVariable String tipoAlerta) {
        try {
            Long negocioId = obtenerNegocioId();
            TipoAlerta tipo = TipoAlerta.valueOf(tipoAlerta);
            List<AlertaInventario> alertas = alertaService.listarAlertasPorTipo(negocioId, tipo);
            return ResponseEntity.ok(alertas);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Tipo de alerta inválido: " + tipoAlerta +
                    ". Valores válidos: stock_bajo, sobrestock, proximo_vencer, vencido, punto_reorden");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/inventario/alertas
     * Crear una alerta de inventario manualmente.
     */
    @PostMapping
    public ResponseEntity<?> crearAlerta(@RequestBody AlertaCreateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            AlertaInventario alerta = alertaService.crearAlerta(negocioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Alerta creada exitosamente");
            response.put("alerta", alerta);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/inventario/alertas/{id}/resolver
     * Marcar una alerta como resuelta.
     */
    @PostMapping("/{id}/resolver")
    public ResponseEntity<?> resolverAlerta(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            AlertaInventario alerta = alertaService.resolverAlerta(negocioId, id);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Alerta resuelta exitosamente");
            response.put("alerta", alerta);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/inventario/alertas/verificar
     * Ejecutar verificación de todas las alertas del negocio bajo demanda.
     */
    @PostMapping("/verificar")
    public ResponseEntity<?> verificarAlertas() {
        try {
            Long negocioId = obtenerNegocioId();
            alertaService.verificarTodasLasAlertas(negocioId);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Verificación de alertas completada exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /restful/inventario/alertas/{id}
     * Actualizar una alerta de inventario.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAlerta(@PathVariable Long id, @RequestBody AlertaUpdateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            AlertaInventario alerta = alertaService.actualizarAlerta(negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Alerta actualizada exitosamente");
            response.put("alerta", alerta);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /restful/inventario/alertas/{id}
     * Eliminar alerta (borrado lógico).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAlerta(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            alertaService.eliminarAlerta(negocioId, id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Alerta eliminada exitosamente (borrado lógico)");
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
