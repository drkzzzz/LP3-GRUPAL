package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.*;
import DrinkGo.DrinkGo_backend.service.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de gastos e ingresos.
 * Endpoints:
 * - GET    /gastos              - Listar todos los gastos
 * - GET    /gastos/{id}         - Obtener un gasto por ID
 * - POST   /gastos              - Crear nuevo gasto
 * - PUT    /gastos/{id}         - Actualizar gasto
 * - PUT    /gastos/{id}/aprobar - Aprobar gasto
 * - PUT    /gastos/{id}/rechazar- Rechazar gasto
 * - PUT    /gastos/{id}/pagar   - Marcar como pagado
 * - PUT    /gastos/{id}/anular  - Anular gasto
 * - DELETE /gastos/{id}         - Eliminar gasto
 */
@RestController
@RequestMapping("/restful/gastos")
@CrossOrigin(origins = "*")
public class GastoController {

    @Autowired
    private GastoService gastoService;

    /**
     * GET /gastos
     * Listar todos los gastos de un negocio, opcionalmente filtrados por sede.
     */
    @GetMapping
    public ResponseEntity<?> listarGastos(
            @RequestParam(name = "negocioId", required = true) Long negocioId,
            @RequestParam(name = "sedeId", required = false) Long sedeId) {
        try {
            List<GastoResponse> gastos = gastoService.obtenerTodos(negocioId, sedeId);
            return ResponseEntity.ok(gastos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /gastos/{id}
     * Obtener un gasto específico por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerGasto(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId) {
        try {
            GastoResponse gasto = gastoService.obtenerPorId(id, negocioId);
            return ResponseEntity.ok(gasto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener el gasto"));
        }
    }

    /**
     * POST /gastos
     * Crear un nuevo gasto.
     */
    @PostMapping
    public ResponseEntity<?> crearGasto(@RequestBody CreateGastoRequest request) {
        try {
            GastoResponse gastoCreado = gastoService.crearGasto(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(gastoCreado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear el gasto: " + e.getMessage()));
        }
    }

    /**
     * PUT /gastos/{id}
     * Actualizar un gasto existente (solo si está en estado pendiente).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarGasto(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId,
            @RequestBody UpdateGastoRequest request) {
        try {
            GastoResponse gastoActualizado = gastoService.actualizarGasto(id, negocioId, request);
            return ResponseEntity.ok(gastoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el gasto"));
        }
    }

    /**
     * PUT /gastos/{id}/aprobar
     * Aprobar un gasto pendiente.
     */
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarGasto(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId,
            @RequestBody AprobarGastoRequest request) {
        try {
            GastoResponse gastoAprobado = gastoService.aprobarGasto(id, negocioId, request);
            return ResponseEntity.ok(gastoAprobado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al aprobar el gasto"));
        }
    }

    /**
     * PUT /gastos/{id}/rechazar
     * Rechazar un gasto pendiente.
     */
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarGasto(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId,
            @RequestBody AprobarGastoRequest request) {
        try {
            GastoResponse gastoRechazado = gastoService.rechazarGasto(id, negocioId, request);
            return ResponseEntity.ok(gastoRechazado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al rechazar el gasto"));
        }
    }

    /**
     * PUT /gastos/{id}/pagar
     * Marcar un gasto aprobado como pagado.
     */
    @PutMapping("/{id}/pagar")
    public ResponseEntity<?> marcarComoPagado(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId) {
        try {
            GastoResponse gastoPagado = gastoService.marcarComoPagado(id, negocioId);
            return ResponseEntity.ok(gastoPagado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al marcar el gasto como pagado"));
        }
    }

    /**
     * PUT /gastos/{id}/anular
     * Anular un gasto.
     */
    @PutMapping("/{id}/anular")
    public ResponseEntity<?> anularGasto(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId) {
        try {
            GastoResponse gastoAnulado = gastoService.anularGasto(id, negocioId);
            return ResponseEntity.ok(gastoAnulado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al anular el gasto"));
        }
    }

    /**
     * DELETE /gastos/{id}
     * Eliminar un gasto (solo si está en estado pendiente).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarGasto(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId) {
        try {
            gastoService.eliminarGasto(id, negocioId);
            return ResponseEntity.ok(Map.of("mensaje", "Gasto eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar el gasto"));
        }
    }
}
