package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.Notificacion;
import DrinkGo.DrinkGo_backend.service.NotificacionService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Notificaciones
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final UsuarioService usuarioService;

    public NotificacionController(NotificacionService notificacionService, UsuarioService usuarioService) {
        this.notificacionService = notificacionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listarPorNegocio() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(notificacionService.findByNegocio(negocioId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Notificacion>> listarPorUsuario(@PathVariable Long usuarioId) {
        verificarAutenticacion();
        return ResponseEntity.ok(notificacionService.findByUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public ResponseEntity<List<Notificacion>> listarNoLeidas(@PathVariable Long usuarioId) {
        verificarAutenticacion();
        return ResponseEntity.ok(notificacionService.findByUsuarioNoLeidas(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> obtener(@PathVariable Long id) {
        verificarAutenticacion();
        return ResponseEntity.ok(notificacionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Notificacion> crear(@RequestBody Notificacion notificacion) {
        Long negocioId = obtenerNegocioId();
        notificacion.setNegocioId(negocioId);
        return ResponseEntity.ok(notificacionService.crear(notificacion));
    }

    @PutMapping("/{id}/marcar-leida")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable Long id) {
        verificarAutenticacion();
        return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
    }

    @PutMapping("/usuario/{usuarioId}/marcar-todas-leidas")
    public ResponseEntity<Void> marcarTodasComoLeidas(@PathVariable Long usuarioId) {
        verificarAutenticacion();
        notificacionService.marcarTodasComoLeidas(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        verificarAutenticacion();
        notificacionService.eliminar(id);
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
