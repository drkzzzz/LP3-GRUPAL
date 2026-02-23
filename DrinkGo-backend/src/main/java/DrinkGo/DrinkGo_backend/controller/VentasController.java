package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Ventas;
import DrinkGo.DrinkGo_backend.service.IVentasService;
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
public class VentasController {
    @Autowired
    private IVentasService service;

    @GetMapping("/ventas")
    public List<Ventas> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/ventas")
    public Ventas guardar(@RequestBody Ventas entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/ventas")
    public Ventas modificar(@RequestBody Ventas entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/ventas/{id}")
    public Optional<Ventas> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/ventas/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
