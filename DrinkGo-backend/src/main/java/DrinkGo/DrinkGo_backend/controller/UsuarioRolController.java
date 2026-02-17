package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.UsuarioRolRequest;
import DrinkGo.DrinkGo_backend.dto.UsuarioRolResponse;
import DrinkGo.DrinkGo_backend.service.UsuarioRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de Usuarios-Roles
 * RF-ADM-018
 */
@RestController
@RequestMapping("/api/usuarios-roles")
@CrossOrigin(origins = "*")
public class UsuarioRolController {

    @Autowired
    private UsuarioRolService usuarioRolService;

    @GetMapping
    public ResponseEntity<List<UsuarioRolResponse>> obtenerTodasLasAsignaciones() {
        return ResponseEntity.ok(usuarioRolService.obtenerTodasLasAsignaciones());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<UsuarioRolResponse>> obtenerRolesPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(usuarioRolService.obtenerRolesPorUsuario(usuarioId));
    }

    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<UsuarioRolResponse>> obtenerUsuariosPorRol(@PathVariable Long rolId) {
        return ResponseEntity.ok(usuarioRolService.obtenerUsuariosPorRol(rolId));
    }

    @GetMapping("/vigentes")
    public ResponseEntity<List<UsuarioRolResponse>> obtenerAsignacionesVigentes() {
        return ResponseEntity.ok(usuarioRolService.obtenerAsignacionesVigentes());
    }

    @GetMapping("/usuario/{usuarioId}/vigentes")
    public ResponseEntity<List<UsuarioRolResponse>> obtenerRolesVigentesPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(usuarioRolService.obtenerRolesVigentesPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRolResponse> obtenerAsignacionPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioRolService.obtenerAsignacionPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioRolResponse> asignarRolAUsuario(@RequestBody UsuarioRolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioRolService.asignarRolAUsuario(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioRolResponse> actualizarAsignacion(
            @PathVariable Long id,
            @RequestBody UsuarioRolRequest request) {
        return ResponseEntity.ok(usuarioRolService.actualizarAsignacion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsignacion(@PathVariable Long id) {
        usuarioRolService.eliminarAsignacion(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{usuarioId}/rol/{rolId}")
    public ResponseEntity<Void> eliminarRolDeUsuario(
            @PathVariable Long usuarioId,
            @PathVariable Long rolId) {
        usuarioRolService.eliminarRolDeUsuario(usuarioId, rolId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{usuarioId}/todos")
    public ResponseEntity<Void> eliminarTodosLosRolesDelUsuario(@PathVariable Long usuarioId) {
        usuarioRolService.eliminarTodosLosRolesDelUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
