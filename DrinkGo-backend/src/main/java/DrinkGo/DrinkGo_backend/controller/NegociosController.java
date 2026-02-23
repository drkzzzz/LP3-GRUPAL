package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Negocios;
import DrinkGo.DrinkGo_backend.service.INegociosService;
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
public class NegociosController {
    @Autowired
    private INegociosService service;

    @GetMapping("/negocios")
    public List<Negocios> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/negocios")
    public Negocios guardar(@RequestBody Negocios entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/negocios")
    public Negocios modificar(@RequestBody Negocios entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/negocios/{id}")
    public Optional<Negocios> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/negocios/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
