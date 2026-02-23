package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Gastos;
import DrinkGo.DrinkGo_backend.service.IGastosService;
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
public class GastosController {
    @Autowired
    private IGastosService service;

    @GetMapping("/gastos")
    public List<Gastos> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/gastos")
    public Gastos guardar(@RequestBody Gastos entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/gastos")
    public Gastos modificar(@RequestBody Gastos entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/gastos/{id}")
    public Optional<Gastos> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/gastos/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
