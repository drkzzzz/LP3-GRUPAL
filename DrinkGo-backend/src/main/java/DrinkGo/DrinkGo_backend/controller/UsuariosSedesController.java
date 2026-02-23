package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.UsuariosSedes;
import DrinkGo.DrinkGo_backend.service.IUsuariosSedesService;
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
public class UsuariosSedesController {
    @Autowired
    private IUsuariosSedesService service;

    @GetMapping("/usuarios-sedes")
    public List<UsuariosSedes> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/usuarios-sedes")
    public UsuariosSedes guardar(@RequestBody UsuariosSedes entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/usuarios-sedes")
    public UsuariosSedes modificar(@RequestBody UsuariosSedes entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/usuarios-sedes/{id}")
    public Optional<UsuariosSedes> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/usuarios-sedes/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
