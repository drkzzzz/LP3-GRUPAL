package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.UsuariosPlataforma;
import DrinkGo.DrinkGo_backend.service.IUsuariosPlataformaService;
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
public class UsuariosPlataformaController {
    @Autowired
    private IUsuariosPlataformaService service;

    @GetMapping("/usuarios-plataforma")
    public List<UsuariosPlataforma> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/usuarios-plataforma")
    public UsuariosPlataforma guardar(@RequestBody UsuariosPlataforma entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/usuarios-plataforma")
    public UsuariosPlataforma modificar(@RequestBody UsuariosPlataforma entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/usuarios-plataforma/{id}")
    public Optional<UsuariosPlataforma> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/usuarios-plataforma/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
