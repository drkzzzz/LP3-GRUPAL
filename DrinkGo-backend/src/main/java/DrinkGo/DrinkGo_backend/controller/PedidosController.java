package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Pedidos;
import DrinkGo.DrinkGo_backend.service.IPedidosService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/restful")
public class PedidosController {
    @Autowired
    private IPedidosService service;

    @GetMapping("/pedidos")
    public List<Pedidos> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/pedidos")
    public Pedidos guardar(@RequestBody Pedidos entity) {
        System.out.println("🔵 [POST /pedidos] Recibido:");
        System.out.println("  - Cliente: " + (entity.getCliente() != null ? entity.getCliente().getId() : "NULL"));
        System.out.println("  - Tipo pedido: " + entity.getTipoPedido());
        System.out.println("  - Total: " + entity.getTotal());
        System.out.println("  - Distrito: " + entity.getDistrito());
        System.out.println("  - Zona delivery ID: " + (entity.getZonaDelivery() != null ? entity.getZonaDelivery().getId() : "NULL"));
        System.out.println("  - Negocio ID: " + (entity.getNegocio() != null ? entity.getNegocio().getId() : "NULL"));
        System.out.println("  - Sede ID: " + (entity.getSede() != null ? entity.getSede().getId() : "NULL"));
        
        service.guardar(entity);
        
        System.out.println("✅ Pedido guardado con ID: " + entity.getId() + " - Número: " + entity.getNumeroPedido());
        return entity;
    }

    @PutMapping("/pedidos")
    public Pedidos modificar(@RequestBody Pedidos entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/pedidos/{id}")
    public Optional<Pedidos> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/pedidos/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }

    /**
     * Cambia solo el estado del pedido. Carga el registro completo desde BD
     * para no sobrescribir relaciones (negocio, sede, cliente).
     * Body: { "estado": "confirmado" }
     */
    @PatchMapping("/pedidos/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> body) {
        try {
            String nuevoEstado = body.get("estado");
            if (nuevoEstado == null || nuevoEstado.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El campo 'estado' es requerido"));
            }
            service.cambiarEstado(id, nuevoEstado);
            // Devolver solo un mapa simple para evitar LazyInitializationException al serializar
            return ResponseEntity.ok(Map.of("id", id, "estado", nuevoEstado, "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error al cambiar estado pedido " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
