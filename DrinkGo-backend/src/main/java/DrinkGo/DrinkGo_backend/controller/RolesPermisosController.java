package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.RolesPermisos;
import DrinkGo.DrinkGo_backend.service.IRolesPermisosService;
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
public class RolesPermisosController {
    @Autowired
    private IRolesPermisosService service;

    @GetMapping("/roles-permisos")
    public List<RolesPermisos> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/roles-permisos")
    public RolesPermisos guardar(@RequestBody RolesPermisos entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/roles-permisos")
    public RolesPermisos modificar(@RequestBody RolesPermisos entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/roles-permisos/{id}")
    public Optional<RolesPermisos> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/roles-permisos/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
