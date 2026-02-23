package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Proveedores;
import DrinkGo.DrinkGo_backend.service.IProveedoresService;
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
public class ProveedoresController {
    @Autowired
    private IProveedoresService service;

    @GetMapping("/proveedores")
    public List<Proveedores> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/proveedores")
    public Proveedores guardar(@RequestBody Proveedores entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/proveedores")
    public Proveedores modificar(@RequestBody Proveedores entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/proveedores/{id}")
    public Optional<Proveedores> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/proveedores/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
