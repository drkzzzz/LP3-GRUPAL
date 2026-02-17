package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.Mesa;
import DrinkGo.DrinkGo_backend.service.MesaService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Mesas
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    private final MesaService mesaService;
    private final UsuarioService usuarioService;

    public MesaController(MesaService mesaService, UsuarioService usuarioService) {
        this.mesaService = mesaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Mesa>> listarPorNegocio() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(mesaService.findByNegocio(negocioId));
    }

    @GetMapping("/sede/{sedeId}")
    public ResponseEntity<List<Mesa>> listarPorSede(@PathVariable Long sedeId) {
        verificarAutenticacion();
        return ResponseEntity.ok(mesaService.findBySede(sedeId));
    }

    @GetMapping("/sede/{sedeId}/activas")
    public ResponseEntity<List<Mesa>> listarActivasPorSede(@PathVariable Long sedeId) {
        verificarAutenticacion();
        return ResponseEntity.ok(mesaService.findBySedeActivas(sedeId));
    }

    @GetMapping("/sede/{sedeId}/estado/{estado}")
    public ResponseEntity<List<Mesa>> listarPorSedeYEstado(@PathVariable Long sedeId, @PathVariable String estado) {
        verificarAutenticacion();
        return ResponseEntity.ok(mesaService.findBySedeYEstado(sedeId, estado));
    }

    @GetMapping("/area/{areaId}")
    public ResponseEntity<List<Mesa>> listarPorArea(@PathVariable Long areaId) {
        verificarAutenticacion();
        return ResponseEntity.ok(mesaService.findByArea(areaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mesa> obtener(@PathVariable Long id) {
        verificarAutenticacion();
        return ResponseEntity.ok(mesaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Mesa> crear(@RequestBody Mesa mesa) {
        Long negocioId = obtenerNegocioId();
        mesa.setNegocioId(negocioId);
        return ResponseEntity.ok(mesaService.crear(mesa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mesa> actualizar(@PathVariable Long id, @RequestBody Mesa mesa) {
        verificarAutenticacion();
        return ResponseEntity.ok(mesaService.actualizar(id, mesa));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Mesa> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        verificarAutenticacion();
        return ResponseEntity.ok(mesaService.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        verificarAutenticacion();
        mesaService.eliminar(id);
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
