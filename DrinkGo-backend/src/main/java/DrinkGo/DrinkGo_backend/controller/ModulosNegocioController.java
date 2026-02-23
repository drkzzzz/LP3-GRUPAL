package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.ModulosNegocio;
import DrinkGo.DrinkGo_backend.service.IModulosNegocioService;
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
public class ModulosNegocioController {
    @Autowired
    private IModulosNegocioService service;

    @GetMapping("/modulos-negocio")
    public List<ModulosNegocio> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/modulos-negocio")
    public ModulosNegocio guardar(@RequestBody ModulosNegocio entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/modulos-negocio")
    public ModulosNegocio modificar(@RequestBody ModulosNegocio entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/modulos-negocio/{id}")
    public Optional<ModulosNegocio> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/modulos-negocio/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
