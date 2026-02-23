package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.SeguimientoPedidos;
import DrinkGo.DrinkGo_backend.service.ISeguimientoPedidosService;
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
public class SeguimientoPedidosController {
    @Autowired
    private ISeguimientoPedidosService service;

    @GetMapping("/seguimiento-pedidos")
    public List<SeguimientoPedidos> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/seguimiento-pedidos")
    public SeguimientoPedidos guardar(@RequestBody SeguimientoPedidos entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/seguimiento-pedidos")
    public SeguimientoPedidos modificar(@RequestBody SeguimientoPedidos entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/seguimiento-pedidos/{id}")
    public Optional<SeguimientoPedidos> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/seguimiento-pedidos/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
