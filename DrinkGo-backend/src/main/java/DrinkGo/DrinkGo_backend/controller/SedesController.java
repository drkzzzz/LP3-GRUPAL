package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Sedes;
import DrinkGo.DrinkGo_backend.service.ISedesService;
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
public class SedesController {
    @Autowired
    private ISedesService service;

    @GetMapping("/sedes")
    public List<Sedes> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/sedes")
    public Sedes guardar(@RequestBody Sedes entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/sedes")
    public Sedes modificar(@RequestBody Sedes entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/sedes/{id}")
    public Optional<Sedes> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/sedes/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
