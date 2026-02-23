package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.SesionesUsuario;
import DrinkGo.DrinkGo_backend.service.ISesionesUsuarioService;
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
public class SesionesUsuarioController {
    @Autowired
    private ISesionesUsuarioService service;

    @GetMapping("/sesiones-usuario")
    public List<SesionesUsuario> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/sesiones-usuario")
    public SesionesUsuario guardar(@RequestBody SesionesUsuario entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/sesiones-usuario")
    public SesionesUsuario modificar(@RequestBody SesionesUsuario entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/sesiones-usuario/{id}")
    public Optional<SesionesUsuario> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/sesiones-usuario/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
