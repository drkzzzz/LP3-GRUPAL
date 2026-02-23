package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Marcas;
import DrinkGo.DrinkGo_backend.service.IMarcasService;
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
public class MarcasController {
    @Autowired
    private IMarcasService service;

    @GetMapping("/marcas")
    public List<Marcas> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/marcas")
    public Marcas guardar(@RequestBody Marcas entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/marcas")
    public Marcas modificar(@RequestBody Marcas entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/marcas/{id}")
    public Optional<Marcas> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/marcas/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
