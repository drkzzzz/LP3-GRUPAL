package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.RolPermisoRequest;
import DrinkGo.DrinkGo_backend.dto.RolPermisoResponse;
import DrinkGo.DrinkGo_backend.service.RolPermisoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de Roles-Permisos
 * RF-ADM-017
 */
@RestController
@RequestMapping("/api/roles-permisos")
@CrossOrigin(origins = "*")
public class RolPermisoController {

    @Autowired
    private RolPermisoService rolPermisoService;

    @GetMapping
    public ResponseEntity<List<RolPermisoResponse>> obtenerTodasLasAsignaciones() {
        return ResponseEntity.ok(rolPermisoService.obtenerTodasLasAsignaciones());
    }

    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<RolPermisoResponse>> obtenerPermisosPorRol(@PathVariable Long rolId) {
        return ResponseEntity.ok(rolPermisoService.obtenerPermisosPorRol(rolId));
    }

    @GetMapping("/permiso/{permisoId}")
    public ResponseEntity<List<RolPermisoResponse>> obtenerRolesPorPermiso(@PathVariable Long permisoId) {
        return ResponseEntity.ok(rolPermisoService.obtenerRolesPorPermiso(permisoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolPermisoResponse> obtenerAsignacionPorId(@PathVariable Long id) {
        return ResponseEntity.ok(rolPermisoService.obtenerAsignacionPorId(id));
    }

    @PostMapping
    public ResponseEntity<RolPermisoResponse> asignarPermisoARol(@RequestBody RolPermisoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rolPermisoService.asignarPermisoARol(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolPermisoResponse> actualizarAsignacion(
            @PathVariable Long id,
            @RequestBody RolPermisoRequest request) {
        return ResponseEntity.ok(rolPermisoService.actualizarAsignacion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsignacion(@PathVariable Long id) {
        rolPermisoService.eliminarAsignacion(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rol/{rolId}/permiso/{permisoId}")
    public ResponseEntity<Void> eliminarPermisoDeRol(
            @PathVariable Long rolId,
            @PathVariable Long permisoId) {
        rolPermisoService.eliminarPermisoDeRol(rolId, permisoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rol/{rolId}/todos")
    public ResponseEntity<Void> eliminarTodosLosPermisosDelRol(@PathVariable Long rolId) {
        rolPermisoService.eliminarTodosLosPermisosDelRol(rolId);
        return ResponseEntity.noContent().build();
    }
}
