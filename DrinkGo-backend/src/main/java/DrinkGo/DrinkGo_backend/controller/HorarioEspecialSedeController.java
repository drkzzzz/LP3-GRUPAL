package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.HorarioEspecialSede;
import DrinkGo.DrinkGo_backend.service.HorarioEspecialSedeService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para Horarios Especiales de Sede
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/horarios-especiales")
public class HorarioEspecialSedeController {

    private final HorarioEspecialSedeService horarioEspecialService;
    private final UsuarioService usuarioService;

    public HorarioEspecialSedeController(HorarioEspecialSedeService horarioEspecialService,
            UsuarioService usuarioService) {
        this.horarioEspecialService = horarioEspecialService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/sede/{sedeId}")
    public ResponseEntity<List<HorarioEspecialSede>> listarPorSede(@PathVariable Long sedeId) {
        verificarAutenticacion();
        return ResponseEntity.ok(horarioEspecialService.findBySede(sedeId));
    }

    @GetMapping("/sede/{sedeId}/rango")
    public ResponseEntity<List<HorarioEspecialSede>> listarPorSedeEnRango(
            @PathVariable Long sedeId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        verificarAutenticacion();
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        return ResponseEntity.ok(horarioEspecialService.findBySedeEnRango(sedeId, inicio, fin));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioEspecialSede> obtener(@PathVariable Long id) {
        verificarAutenticacion();
        return ResponseEntity.ok(horarioEspecialService.findById(id));
    }

    @PostMapping
    public ResponseEntity<HorarioEspecialSede> crear(@RequestBody HorarioEspecialSede horario) {
        verificarAutenticacion();
        return ResponseEntity.ok(horarioEspecialService.crear(horario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioEspecialSede> actualizar(@PathVariable Long id,
            @RequestBody HorarioEspecialSede horario) {
        verificarAutenticacion();
        return ResponseEntity.ok(horarioEspecialService.actualizar(id, horario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        verificarAutenticacion();
        horarioEspecialService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void verificarAutenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
    }
}
