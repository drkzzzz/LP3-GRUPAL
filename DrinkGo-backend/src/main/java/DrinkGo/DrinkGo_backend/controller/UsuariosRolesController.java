package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.UsuariosRoles;
import DrinkGo.DrinkGo_backend.repository.UsuariosRolesRepository;
import DrinkGo.DrinkGo_backend.service.IUsuariosRolesService;
import java.util.List;
import java.util.Map;
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
public class UsuariosRolesController {
    @Autowired
    private IUsuariosRolesService service;

    @Autowired
    private UsuariosRolesRepository repo;

    @GetMapping("/usuarios-roles")
    public List<UsuariosRoles> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/usuarios-roles")
    public Map<String, Object> guardar(@RequestBody UsuariosRoles entity) {
        service.guardar(entity);
        // Recargar con JOIN FETCH para que la serialización no dispare lazy loading
        List<UsuariosRoles> recargados = repo.findByUsuarioId(entity.getUsuario().getId());
        UsuariosRoles guardado = recargados.stream()
                .filter(ur -> ur.getId().equals(entity.getId()))
                .findFirst()
                .orElse(entity);
        return Map.of(
                "id", guardado.getId(),
                "usuarioId", guardado.getUsuarioId() != null ? guardado.getUsuarioId() : 0,
                "rolId", guardado.getRolId() != null ? guardado.getRolId() : 0,
                "rolNombre", guardado.getRolNombre() != null ? guardado.getRolNombre() : "");
    }

    @PutMapping("/usuarios-roles")
    public UsuariosRoles modificar(@RequestBody UsuariosRoles entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/usuarios-roles/{id}")
    public Optional<UsuariosRoles> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/usuarios-roles/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
