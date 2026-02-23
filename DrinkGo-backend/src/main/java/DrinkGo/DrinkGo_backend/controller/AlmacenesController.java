package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Almacenes;
import DrinkGo.DrinkGo_backend.service.IAlmacenesService;
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
public class AlmacenesController {
    @Autowired
    private IAlmacenesService service;

    @GetMapping("/almacenes")
    public List<Almacenes> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/almacenes")
    public Almacenes guardar(@RequestBody Almacenes entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/almacenes")
    public Almacenes modificar(@RequestBody Almacenes entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/almacenes/{id}")
    public Optional<Almacenes> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/almacenes/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
