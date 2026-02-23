package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.DetallePedidos;
import DrinkGo.DrinkGo_backend.service.IDetallePedidosService;
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
public class DetallePedidosController {
    @Autowired
    private IDetallePedidosService service;

    @GetMapping("/detalle-pedidos")
    public List<DetallePedidos> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/detalle-pedidos")
    public DetallePedidos guardar(@RequestBody DetallePedidos entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/detalle-pedidos")
    public DetallePedidos modificar(@RequestBody DetallePedidos entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/detalle-pedidos/{id}")
    public Optional<DetallePedidos> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/detalle-pedidos/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
