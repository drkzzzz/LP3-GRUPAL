package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.UsuarioSede;
import DrinkGo.DrinkGo_backend.service.UsuarioSedeService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para asignación Usuario-Sede
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/usuario-sede")
public class UsuarioSedeController {

    private final UsuarioSedeService usuarioSedeService;
    private final UsuarioService usuarioService;

    public UsuarioSedeController(UsuarioSedeService usuarioSedeService, UsuarioService usuarioService) {
        this.usuarioSedeService = usuarioSedeService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioSede>> listarTodos() {
        verificarAutenticacion();
        return ResponseEntity.ok(usuarioSedeService.findAll());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<UsuarioSede>> listarPorUsuario(@PathVariable Long usuarioId) {
        verificarAutenticacion();
        return ResponseEntity.ok(usuarioSedeService.findByUsuario(usuarioId));
    }

    @GetMapping("/sede/{sedeId}")
    public ResponseEntity<List<UsuarioSede>> listarPorSede(@PathVariable Long sedeId) {
        verificarAutenticacion();
        return ResponseEntity.ok(usuarioSedeService.findBySede(sedeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioSede> obtener(@PathVariable Long id) {
        verificarAutenticacion();
        return ResponseEntity.ok(usuarioSedeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioSede> crear(@RequestBody UsuarioSede usuarioSede) {
        verificarAutenticacion();
        return ResponseEntity.ok(usuarioSedeService.crear(usuarioSede));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioSede> actualizar(@PathVariable Long id, @RequestBody UsuarioSede usuarioSede) {
        verificarAutenticacion();
        return ResponseEntity.ok(usuarioSedeService.actualizar(id, usuarioSede));
    }

    @PutMapping("/establecer-predeterminada")
    public ResponseEntity<UsuarioSede> establecerPredeterminada(@RequestParam Long usuarioId,
            @RequestParam Long sedeId) {
        verificarAutenticacion();
        return ResponseEntity.ok(usuarioSedeService.establecerPredeterminada(usuarioId, sedeId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        verificarAutenticacion();
        usuarioSedeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void verificarAutenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
    }
}
