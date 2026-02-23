package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.PermisosSistema;
import DrinkGo.DrinkGo_backend.service.IPermisosSistemaService;
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
public class PermisosSistemaController {
    @Autowired
    private IPermisosSistemaService service;

    @GetMapping("/permisos-sistema")
    public List<PermisosSistema> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/permisos-sistema")
    public PermisosSistema guardar(@RequestBody PermisosSistema entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/permisos-sistema")
    public PermisosSistema modificar(@RequestBody PermisosSistema entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/permisos-sistema/{id}")
    public Optional<PermisosSistema> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/permisos-sistema/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
