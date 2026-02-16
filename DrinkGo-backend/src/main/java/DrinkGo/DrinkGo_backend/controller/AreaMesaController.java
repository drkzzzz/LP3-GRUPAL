package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.AreaMesa;
import DrinkGo.DrinkGo_backend.service.AreaMesaService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Áreas de Mesa
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/areas-mesa")
public class AreaMesaController {

    private final AreaMesaService areaMesaService;
    private final UsuarioService usuarioService;

    public AreaMesaController(AreaMesaService areaMesaService, UsuarioService usuarioService) {
        this.areaMesaService = areaMesaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<AreaMesa>> listarPorNegocio() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(areaMesaService.findByNegocio(negocioId));
    }

    @GetMapping("/sede/{sedeId}")
    public ResponseEntity<List<AreaMesa>> listarPorSede(@PathVariable Long sedeId) {
        verificarAutenticacion();
        return ResponseEntity.ok(areaMesaService.findBySede(sedeId));
    }

    @GetMapping("/sede/{sedeId}/activas")
    public ResponseEntity<List<AreaMesa>> listarActivasPorSede(@PathVariable Long sedeId) {
        verificarAutenticacion();
        return ResponseEntity.ok(areaMesaService.findBySedeActivas(sedeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaMesa> obtener(@PathVariable Long id) {
        verificarAutenticacion();
        return ResponseEntity.ok(areaMesaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AreaMesa> crear(@RequestBody AreaMesa areaMesa) {
        Long negocioId = obtenerNegocioId();
        areaMesa.setNegocioId(negocioId);
        return ResponseEntity.ok(areaMesaService.crear(areaMesa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaMesa> actualizar(@PathVariable Long id, @RequestBody AreaMesa areaMesa) {
        verificarAutenticacion();
        return ResponseEntity.ok(areaMesaService.actualizar(id, areaMesa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        verificarAutenticacion();
        areaMesaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerNegocioId(uuid);
    }

    private void verificarAutenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
    }
}
