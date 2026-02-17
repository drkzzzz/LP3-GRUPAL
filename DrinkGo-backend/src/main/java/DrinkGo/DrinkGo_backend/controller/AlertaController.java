package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.AlertaCreateRequest;
import DrinkGo.DrinkGo_backend.dto.AlertaInventarioResponse;
import DrinkGo.DrinkGo_backend.dto.AlertaUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.AlertaInventario;
import DrinkGo.DrinkGo_backend.entity.AlertaInventario.TipoAlerta;
import DrinkGo.DrinkGo_backend.service.AlertaInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de Alertas de Inventario - Bloque 5.
 * Unificado: delega a AlertaInventarioService.
 * Implementa RF-INV-004 y RF-INV-005.
 * negocioId hardcoded = 1L (JWT eliminado para entorno de desarrollo).
 */
@RestController
@RequestMapping("/restful/inventario/alertas")
public class AlertaController {

    private static final Long NEGOCIO_ID = 1L;
    private static final Long USUARIO_ID = 1L;

    @Autowired
    private AlertaInventarioService alertaService;

    /** GET /restful/inventario/alertas — Listar alertas activas */
    @GetMapping
    public ResponseEntity<?> listarAlertasActivas() {
        try {
            List<AlertaInventarioResponse> alertas = alertaService.listarActivas(NEGOCIO_ID);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/alertas/todas — Listar todas (activas y resueltas) */
    @GetMapping("/todas")
    public ResponseEntity<?> listarTodasLasAlertas() {
        try {
            List<AlertaInventarioResponse> alertas = alertaService.listar(NEGOCIO_ID);
            return ResponseEntity.ok(alertas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/alertas/{id} — Obtener alerta por ID */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAlerta(@PathVariable Long id) {
        try {
            AlertaInventarioResponse alerta = alertaService.obtenerPorId(id, NEGOCIO_ID);
            return ResponseEntity.ok(alerta);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/alertas/tipo/{tipoAlerta} — Filtrar por tipo */
    @GetMapping("/tipo/{tipoAlerta}")
    public ResponseEntity<?> listarAlertasPorTipo(@PathVariable String tipoAlerta) {
        try {
            TipoAlerta tipo = TipoAlerta.valueOf(tipoAlerta);
            List<AlertaInventario> alertas = alertaService.listarAlertasPorTipo(NEGOCIO_ID, tipo);
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

    /** POST /restful/inventario/alertas — Crear alerta manualmente */
    @PostMapping
    public ResponseEntity<?> crearAlerta(@RequestBody AlertaCreateRequest request) {
        try {
            AlertaInventario alerta = alertaService.crearAlerta(NEGOCIO_ID, request);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Alerta creada exitosamente");
            response.put("alerta", alerta);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** POST /restful/inventario/alertas/{id}/resolver — Marcar como resuelta */
    @PostMapping("/{id}/resolver")
    public ResponseEntity<?> resolverAlerta(@PathVariable Long id) {
        try {
            alertaService.resolver(id, NEGOCIO_ID, USUARIO_ID);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Alerta resuelta exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** POST /restful/inventario/alertas/verificar — Verificar alertas bajo demanda */
    @PostMapping("/verificar")
    public ResponseEntity<?> verificarAlertas() {
        try {
            alertaService.verificarTodasLasAlertas(NEGOCIO_ID);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Verificación de alertas completada exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** PUT /restful/inventario/alertas/{id} — Actualizar alerta */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAlerta(@PathVariable Long id, @RequestBody AlertaUpdateRequest request) {
        try {
            AlertaInventario alerta = alertaService.actualizarAlerta(NEGOCIO_ID, id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Alerta actualizada exitosamente");
            response.put("alerta", alerta);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** DELETE /restful/inventario/alertas/{id} — Eliminar alerta */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAlerta(@PathVariable Long id) {
        try {
            alertaService.eliminarAlerta(NEGOCIO_ID, id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Alerta eliminada exitosamente");
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
