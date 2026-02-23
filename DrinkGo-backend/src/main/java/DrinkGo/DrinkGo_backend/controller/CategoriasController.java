package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Categorias;
import DrinkGo.DrinkGo_backend.service.ICategoriasService;
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
public class CategoriasController {
    @Autowired
    private ICategoriasService service;

    @GetMapping("/categorias")
    public List<Categorias> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/categorias")
    public Categorias guardar(@RequestBody Categorias entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/categorias")
    public Categorias modificar(@RequestBody Categorias entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/categorias/{id}")
    public Optional<Categorias> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/categorias/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
