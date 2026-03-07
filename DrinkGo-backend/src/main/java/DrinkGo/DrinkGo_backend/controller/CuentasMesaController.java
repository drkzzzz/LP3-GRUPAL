package DrinkGo.DrinkGo_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import DrinkGo.DrinkGo_backend.entity.CuentasMesa;
import DrinkGo.DrinkGo_backend.entity.DetalleCuentaMesa;
import DrinkGo.DrinkGo_backend.service.ICuentasMesaService;

@RestController
@RequestMapping("/restful")
public class CuentasMesaController {

    @Autowired
    private ICuentasMesaService service;

    // ── RF-VTA-011: Abrir cuenta ───────────────────────────────────────

    /**
     * POST /restful/cuentas-mesa
     * Body: { negocio: {id}, mesa: {id}, mesero: {id}, numComensales, cliente: {id}
     * (opcional) }
     */
    @PostMapping("/cuentas-mesa")
    public ResponseEntity<?> abrirCuenta(@RequestBody CuentasMesa cuenta) {
        try {
            CuentasMesa nueva = service.abrirCuenta(cuenta);
            return ResponseEntity.ok(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── Consultas ─────────────────────────────────────────────────────

    /**
     * GET /restful/cuentas-mesa/por-sede/{sedeId}
     * Lista las cuentas abiertas de una sede (para el mapa de mesas).
     */
    @GetMapping("/cuentas-mesa/por-sede/{sedeId}")
    public ResponseEntity<?> buscarAbiertasPorSede(@PathVariable Long sedeId) {
        try {
            return ResponseEntity.ok(service.buscarAbiertasPorSede(sedeId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /restful/cuentas-mesa/{id}
     * Obtiene una cuenta con sus detalles.
     */
    @GetMapping("/cuentas-mesa/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /restful/cuentas-mesa/{id}/detalles
     * Lista los ítems activos de una cuenta.
     */
    @GetMapping("/cuentas-mesa/{id}/detalles")
    public ResponseEntity<?> obtenerDetalles(@PathVariable Long id) {
        try {
            List<DetalleCuentaMesa> detalles = service.obtenerDetalles(id);
            return ResponseEntity.ok(detalles);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── RF-VTA-012: Agregar / quitar productos ────────────────────────

    /**
     * POST /restful/cuentas-mesa/{id}/productos
     * Body: { producto: {id}, cantidad, precioUnitario, notas (opcional) }
     */
    @PostMapping("/cuentas-mesa/{id}/productos")
    public ResponseEntity<?> agregarProducto(@PathVariable Long id,
            @RequestBody DetalleCuentaMesa detalle) {
        try {
            DetalleCuentaMesa nuevo = service.agregarProducto(id, detalle);
            return ResponseEntity.ok(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * DELETE /restful/cuentas-mesa/detalle/{detalleId}
     * Soft-delete de un ítem.
     */
    @DeleteMapping("/cuentas-mesa/detalle/{detalleId}")
    public ResponseEntity<?> removerProducto(@PathVariable Long detalleId) {
        try {
            service.removerProducto(detalleId);
            return ResponseEntity.ok(Map.of("message", "Ítem eliminado"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── RF-VTA-013: Transferir ────────────────────────────────────────

    /**
     * POST /restful/cuentas-mesa/{id}/transferir-productos
     * Body: { cuentaDestinoId: Long, detalleIds: [Long] }
     */
    @PostMapping("/cuentas-mesa/{id}/transferir-productos")
    public ResponseEntity<?> transferirProductos(@PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            Long cuentaDestinoId = Long.valueOf(body.get("cuentaDestinoId").toString());
            @SuppressWarnings("unchecked")
            List<Long> detalleIds = ((List<Integer>) body.get("detalleIds"))
                    .stream().map(Integer::longValue).toList();
            service.transferirProductos(id, cuentaDestinoId, detalleIds);
            return ResponseEntity.ok(Map.of("message", "Productos transferidos"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * POST /restful/cuentas-mesa/{id}/transferir-mesa
     * Body: { nuevaMesaId: Long }
     */
    @PostMapping("/cuentas-mesa/{id}/transferir-mesa")
    public ResponseEntity<?> transferirMesa(@PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            Long nuevaMesaId = Long.valueOf(body.get("nuevaMesaId").toString());
            CuentasMesa actualizada = service.transferirMesa(id, nuevaMesaId);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── RF-VTA-014: Cerrar cuenta ─────────────────────────────────────

    /**
     * GET /restful/cuentas-mesa/{id}/dividir/{personas}
     * Pre-cuenta dividida por número de personas.
     */
    @GetMapping("/cuentas-mesa/{id}/dividir/{personas}")
    public ResponseEntity<?> dividirPorPersonas(@PathVariable Long id,
            @PathVariable int personas) {
        try {
            return ResponseEntity.ok(service.dividirPorPersonas(id, personas));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * POST /restful/cuentas-mesa/{id}/cerrar
     * Body: { usuarioId: Long }
     * Cierra la cuenta y libera la mesa.
     */
    @PostMapping("/cuentas-mesa/{id}/cerrar")
    public ResponseEntity<?> cerrarCuenta(@PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
            CuentasMesa cerrada = service.cerrarCuenta(id, usuarioId);
            return ResponseEntity.ok(cerrada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
