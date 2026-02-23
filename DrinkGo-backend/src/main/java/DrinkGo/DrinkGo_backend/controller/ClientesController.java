package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Clientes;
import DrinkGo.DrinkGo_backend.service.IClientesService;
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
public class ClientesController {
    @Autowired
    private IClientesService service;

    @GetMapping("/clientes")
    public List<Clientes> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/clientes")
    public Clientes guardar(@RequestBody Clientes entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/clientes")
    public Clientes modificar(@RequestBody Clientes entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/clientes/{id}")
    public Optional<Clientes> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/clientes/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
