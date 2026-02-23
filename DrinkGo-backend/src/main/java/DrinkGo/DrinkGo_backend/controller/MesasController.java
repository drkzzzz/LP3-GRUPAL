package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Mesas;
import DrinkGo.DrinkGo_backend.service.IMesasService;
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
public class MesasController {
    @Autowired
    private IMesasService service;

    @GetMapping("/mesas")
    public List<Mesas> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/mesas")
    public Mesas guardar(@RequestBody Mesas entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/mesas")
    public Mesas modificar(@RequestBody Mesas entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/mesas/{id}")
    public Optional<Mesas> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/mesas/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
