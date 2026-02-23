package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Roles;
import DrinkGo.DrinkGo_backend.service.IRolesService;
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
public class RolesController {
    @Autowired
    private IRolesService service;

    @GetMapping("/roles")
    public List<Roles> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/roles")
    public Roles guardar(@RequestBody Roles entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/roles")
    public Roles modificar(@RequestBody Roles entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/roles/{id}")
    public Optional<Roles> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/roles/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
