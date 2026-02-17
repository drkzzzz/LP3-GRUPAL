package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.RestriccionVentaAlcohol;
import DrinkGo.DrinkGo_backend.service.RestriccionVentaAlcoholService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Restricciones de Venta de Alcohol
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/restricciones-alcohol")
public class RestriccionVentaAlcoholController {

    private final RestriccionVentaAlcoholService restriccionService;
    private final UsuarioService usuarioService;

    public RestriccionVentaAlcoholController(RestriccionVentaAlcoholService restriccionService,
            UsuarioService usuarioService) {
        this.restriccionService = restriccionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<RestriccionVentaAlcohol>> listarPorNegocio() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(restriccionService.findByNegocio(negocioId));
    }

    @GetMapping("/activas")
    public ResponseEntity<List<RestriccionVentaAlcohol>> listarActivas() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(restriccionService.findByNegocioActivas(negocioId));
    }

    @GetMapping("/sede/{sedeId}")
    public ResponseEntity<List<RestriccionVentaAlcohol>> listarPorSede(@PathVariable Long sedeId) {
        verificarAutenticacion();
        return ResponseEntity.ok(restriccionService.findBySede(sedeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestriccionVentaAlcohol> obtener(@PathVariable Long id) {
        verificarAutenticacion();
        return ResponseEntity.ok(restriccionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RestriccionVentaAlcohol> crear(@RequestBody RestriccionVentaAlcohol restriccion) {
        Long negocioId = obtenerNegocioId();
        restriccion.setNegocioId(negocioId);
        return ResponseEntity.ok(restriccionService.crear(restriccion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestriccionVentaAlcohol> actualizar(@PathVariable Long id,
            @RequestBody RestriccionVentaAlcohol restriccion) {
        verificarAutenticacion();
        return ResponseEntity.ok(restriccionService.actualizar(id, restriccion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        verificarAutenticacion();
        restriccionService.eliminar(id);
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
