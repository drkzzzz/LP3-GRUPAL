package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Pedidos;
import DrinkGo.DrinkGo_backend.service.IPedidosService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
        
        service.guardar(entity);
        
        System.out.println("✅ Pedido guardado con ID: " + entity.getId());
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
}
