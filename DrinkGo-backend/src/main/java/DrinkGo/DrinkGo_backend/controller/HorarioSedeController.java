package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.HorarioSede;
import DrinkGo.DrinkGo_backend.service.HorarioSedeService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Horarios de Sede
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/horarios-sede")
public class HorarioSedeController {

    private final HorarioSedeService horarioService;
    private final UsuarioService usuarioService;

    public HorarioSedeController(HorarioSedeService horarioService, UsuarioService usuarioService) {
        this.horarioService = horarioService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<HorarioSede>> listarTodos() {
        verificarAutenticacion();
        return ResponseEntity.ok(horarioService.findAll());
    }

    @GetMapping("/sede/{sedeId}")
    public ResponseEntity<List<HorarioSede>> listarPorSede(@PathVariable Long sedeId) {
        verificarAutenticacion();
        return ResponseEntity.ok(horarioService.findBySede(sedeId));
    }

    @GetMapping("/sede/{sedeId}/activos")
    public ResponseEntity<List<HorarioSede>> listarActivosPorSede(@PathVariable Long sedeId) {
        verificarAutenticacion();
        return ResponseEntity.ok(horarioService.findBySedeActivos(sedeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioSede> obtener(@PathVariable Long id) {
        verificarAutenticacion();
        return ResponseEntity.ok(horarioService.findById(id));
    }

    @PostMapping
    public ResponseEntity<HorarioSede> crear(@RequestBody HorarioSede horario) {
        verificarAutenticacion();
        return ResponseEntity.ok(horarioService.crear(horario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioSede> actualizar(@PathVariable Long id, @RequestBody HorarioSede horario) {
        verificarAutenticacion();
        return ResponseEntity.ok(horarioService.actualizar(id, horario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        verificarAutenticacion();
        horarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void verificarAutenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
    }
}
