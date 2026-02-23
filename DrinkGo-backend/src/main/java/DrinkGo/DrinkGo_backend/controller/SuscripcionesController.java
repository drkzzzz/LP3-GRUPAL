package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Suscripciones;
import DrinkGo.DrinkGo_backend.service.ISuscripcionesService;
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
public class SuscripcionesController {
    @Autowired
    private ISuscripcionesService service;

    @GetMapping("/suscripciones")
    public List<Suscripciones> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/suscripciones")
    public Suscripciones guardar(@RequestBody Suscripciones entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/suscripciones")
    public Suscripciones modificar(@RequestBody Suscripciones entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/suscripciones/{id}")
    public Optional<Suscripciones> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/suscripciones/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
