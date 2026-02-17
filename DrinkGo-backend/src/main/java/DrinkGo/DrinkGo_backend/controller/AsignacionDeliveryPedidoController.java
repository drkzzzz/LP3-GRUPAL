package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.AsignacionDeliveryPedido;
import DrinkGo.DrinkGo_backend.service.AsignacionDeliveryPedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/asignaciones-delivery")
public class AsignacionDeliveryPedidoController {

    private final AsignacionDeliveryPedidoService asignacionService;

    public AsignacionDeliveryPedidoController(AsignacionDeliveryPedidoService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignacionDeliveryPedido> obtenerPorId(@PathVariable Long id) {
        Optional<AsignacionDeliveryPedido> asignacion = asignacionService.findById(id);
        return asignacion.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<AsignacionDeliveryPedido> obtenerPorPedido(@PathVariable Long pedidoId) {
        Optional<AsignacionDeliveryPedido> asignacion = asignacionService.findByPedido(pedidoId);
        return asignacion.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/repartidor/{repartidorId}")
    public ResponseEntity<List<AsignacionDeliveryPedido>> listarPorRepartidor(@PathVariable Long repartidorId) {
        return ResponseEntity.ok(asignacionService.findByRepartidor(repartidorId));
    }

    @GetMapping("/repartidor/{repartidorId}/estado/{estado}")
    public ResponseEntity<List<AsignacionDeliveryPedido>> listarPorRepartidorYEstado(
            @PathVariable Long repartidorId,
            @PathVariable AsignacionDeliveryPedido.EstadoDelivery estado) {
        return ResponseEntity.ok(asignacionService.findByRepartidorYEstado(repartidorId, estado));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<AsignacionDeliveryPedido>> listarPorEstado(
            @PathVariable AsignacionDeliveryPedido.EstadoDelivery estado) {
        return ResponseEntity.ok(asignacionService.findByEstado(estado));
    }

    @GetMapping("/repartidor/{repartidorId}/contar/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorRepartidorYEstado(
            @PathVariable Long repartidorId,
            @PathVariable AsignacionDeliveryPedido.EstadoDelivery estado) {
        Long count = asignacionService.contarPorRepartidorYEstado(repartidorId, estado);
        Map<String, Long> response = new HashMap<>();
        response.put("cantidad", count);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AsignacionDeliveryPedido> crear(@RequestBody AsignacionDeliveryPedido asignacion) {
        AsignacionDeliveryPedido nuevaAsignacion = asignacionService.crear(asignacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsignacionDeliveryPedido> actualizar(
            @PathVariable Long id,
            @RequestBody AsignacionDeliveryPedido asignacion) {
        asignacion.setId(id);
        AsignacionDeliveryPedido asignacionActualizada = asignacionService.actualizar(asignacion);
        return ResponseEntity.ok(asignacionActualizada);
    }

    @PatchMapping("/{id}/aceptar")
    public ResponseEntity<AsignacionDeliveryPedido> aceptarPedido(@PathVariable Long id) {
        AsignacionDeliveryPedido asignacion = asignacionService.aceptarPedido(id);
        if (asignacion != null) {
            return ResponseEntity.ok(asignacion);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/marcar-recogido")
    public ResponseEntity<AsignacionDeliveryPedido> marcarRecogido(@PathVariable Long id) {
        AsignacionDeliveryPedido asignacion = asignacionService.marcarRecogido(id);
        if (asignacion != null) {
            return ResponseEntity.ok(asignacion);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/marcar-en-transito")
    public ResponseEntity<AsignacionDeliveryPedido> marcarEnTransito(@PathVariable Long id) {
        AsignacionDeliveryPedido asignacion = asignacionService.marcarEnTransito(id);
        if (asignacion != null) {
            return ResponseEntity.ok(asignacion);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/marcar-entregado")
    public ResponseEntity<AsignacionDeliveryPedido> marcarEntregado(@PathVariable Long id) {
        AsignacionDeliveryPedido asignacion = asignacionService.marcarEntregado(id);
        if (asignacion != null) {
            return ResponseEntity.ok(asignacion);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/marcar-fallido")
    public ResponseEntity<AsignacionDeliveryPedido> marcarFallido(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String notas = request.get("notas");
        AsignacionDeliveryPedido asignacion = asignacionService.marcarFallido(id, notas);
        if (asignacion != null) {
            return ResponseEntity.ok(asignacion);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asignacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
