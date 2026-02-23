package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Devoluciones;
import DrinkGo.DrinkGo_backend.service.IDevolucionesService;
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
public class DevolucionesController {
    @Autowired
    private IDevolucionesService service;

    @GetMapping("/devoluciones")
    public List<Devoluciones> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/devoluciones")
    public Devoluciones guardar(@RequestBody Devoluciones entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/devoluciones")
    public Devoluciones modificar(@RequestBody Devoluciones entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/devoluciones/{id}")
    public Optional<Devoluciones> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/devoluciones/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
