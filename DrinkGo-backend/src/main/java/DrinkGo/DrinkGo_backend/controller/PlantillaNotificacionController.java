package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.PlantillaNotificacion;
import DrinkGo.DrinkGo_backend.service.PlantillaNotificacionService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Plantillas de Notificación
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/plantillas-notificacion")
public class PlantillaNotificacionController {

    private final PlantillaNotificacionService plantillaService;
    private final UsuarioService usuarioService;

    public PlantillaNotificacionController(PlantillaNotificacionService plantillaService,
            UsuarioService usuarioService) {
        this.plantillaService = plantillaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<PlantillaNotificacion>> listarPorNegocio() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(plantillaService.findByNegocio(negocioId));
    }

    @GetMapping("/globales")
    public ResponseEntity<List<PlantillaNotificacion>> listarGlobales() {
        verificarAutenticacion();
        return ResponseEntity.ok(plantillaService.findGlobales());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<PlantillaNotificacion>> listarActivas() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(plantillaService.findByNegocioActivas(negocioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantillaNotificacion> obtener(@PathVariable Long id) {
        verificarAutenticacion();
        return ResponseEntity.ok(plantillaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PlantillaNotificacion> crear(@RequestBody PlantillaNotificacion plantilla) {
        Long negocioId = obtenerNegocioId();
        plantilla.setNegocioId(negocioId);
        return ResponseEntity.ok(plantillaService.crear(plantilla));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantillaNotificacion> actualizar(@PathVariable Long id,
            @RequestBody PlantillaNotificacion plantilla) {
        verificarAutenticacion();
        return ResponseEntity.ok(plantillaService.actualizar(id, plantilla));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        verificarAutenticacion();
        plantillaService.eliminar(id);
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
