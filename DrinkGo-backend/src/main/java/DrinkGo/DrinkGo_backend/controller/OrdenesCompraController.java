package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.OrdenesCompra;
import DrinkGo.DrinkGo_backend.service.IOrdenesCompraService;
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
public class OrdenesCompraController {
    @Autowired
    private IOrdenesCompraService service;

    @GetMapping("/ordenes-compra")
    public List<OrdenesCompra> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/ordenes-compra")
    public OrdenesCompra guardar(@RequestBody OrdenesCompra entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/ordenes-compra")
    public OrdenesCompra modificar(@RequestBody OrdenesCompra entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/ordenes-compra/{id}")
    public Optional<OrdenesCompra> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/ordenes-compra/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
