package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Usuarios;
import DrinkGo.DrinkGo_backend.service.IUsuariosService;
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
public class UsuariosController {
    @Autowired
    private IUsuariosService service;

    @GetMapping("/usuarios")
    public List<Usuarios> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/usuarios")
    public Usuarios guardar(@RequestBody Usuarios entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/usuarios")
    public Usuarios modificar(@RequestBody Usuarios entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/usuarios/{id}")
    public Optional<Usuarios> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/usuarios/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
