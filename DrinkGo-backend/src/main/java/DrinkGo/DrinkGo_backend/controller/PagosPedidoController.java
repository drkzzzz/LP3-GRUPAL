package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.PagosPedido;
import DrinkGo.DrinkGo_backend.service.IPagosPedidoService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PagosPedidoController {
    @Autowired
    private IPagosPedidoService service;

    @GetMapping("/pagos-pedido")
    public List<PagosPedido> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/pagos-pedido")
    public PagosPedido guardar(@RequestBody PagosPedido entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/pagos-pedido")
    public PagosPedido modificar(@RequestBody PagosPedido entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/pagos-pedido/{id}")
    public Optional<PagosPedido> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/pagos-pedido/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }

    /** Obtiene todos los pagos asociados a un pedido */
    @GetMapping("/pagos-pedido/por-pedido/{pedidoId}")
    public List<PagosPedido> porPedido(@PathVariable("pedidoId") Long pedidoId) {
        return service.buscarPorPedido(pedidoId);
    }

    /** Marca un pago como aprobado (pagado) */
    @PatchMapping("/pagos-pedido/{id}/aprobar")
    public ResponseEntity<?> aprobar(@PathVariable("id") Long id) {
        try {
            service.aprobarPago(id);
            return ResponseEntity.ok(Map.of("id", id, "estadoPago", "pagado", "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
