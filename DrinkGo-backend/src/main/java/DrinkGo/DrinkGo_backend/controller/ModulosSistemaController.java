package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.ModulosSistema;
import DrinkGo.DrinkGo_backend.service.IModulosSistemaService;
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
public class ModulosSistemaController {
    @Autowired
    private IModulosSistemaService service;

    @GetMapping("/modulos-sistema")
    public List<ModulosSistema> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/modulos-sistema")
    public ModulosSistema guardar(@RequestBody ModulosSistema entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/modulos-sistema")
    public ModulosSistema modificar(@RequestBody ModulosSistema entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/modulos-sistema/{id}")
    public Optional<ModulosSistema> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/modulos-sistema/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
