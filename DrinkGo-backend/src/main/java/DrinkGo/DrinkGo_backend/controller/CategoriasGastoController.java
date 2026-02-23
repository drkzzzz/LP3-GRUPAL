package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.CategoriasGasto;
import DrinkGo.DrinkGo_backend.service.ICategoriasGastoService;
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
public class CategoriasGastoController {
    @Autowired
    private ICategoriasGastoService service;

    @GetMapping("/categorias-gasto")
    public List<CategoriasGasto> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/categorias-gasto")
    public CategoriasGasto guardar(@RequestBody CategoriasGasto entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/categorias-gasto")
    public CategoriasGasto modificar(@RequestBody CategoriasGasto entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/categorias-gasto/{id}")
    public Optional<CategoriasGasto> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/categorias-gasto/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
