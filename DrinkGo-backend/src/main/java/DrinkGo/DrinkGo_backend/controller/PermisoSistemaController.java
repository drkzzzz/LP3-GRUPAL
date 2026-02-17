package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.PermisoSistemaRequest;
import DrinkGo.DrinkGo_backend.dto.PermisoSistemaResponse;
import DrinkGo.DrinkGo_backend.service.PermisoSistemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de Permisos del Sistema
 * RF-ADM-017
 */
@RestController
@RequestMapping("/api/permisos-sistema")
@CrossOrigin(origins = "*")
public class PermisoSistemaController {

    @Autowired
    private PermisoSistemaService permisoSistemaService;

    @GetMapping
    public ResponseEntity<List<PermisoSistemaResponse>> obtenerTodosLosPermisos() {
        return ResponseEntity.ok(permisoSistemaService.obtenerTodosLosPermisos());
    }

    @GetMapping("/modulo/{moduloId}")
    public ResponseEntity<List<PermisoSistemaResponse>> obtenerPermisosPorModulo(@PathVariable Long moduloId) {
        return ResponseEntity.ok(permisoSistemaService.obtenerPermisosPorModulo(moduloId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermisoSistemaResponse> obtenerPermisoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(permisoSistemaService.obtenerPermisoPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PermisoSistemaResponse> obtenerPermisoPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(permisoSistemaService.obtenerPermisoPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<PermisoSistemaResponse> crearPermiso(@RequestBody PermisoSistemaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(permisoSistemaService.crearPermiso(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermisoSistemaResponse> actualizarPermiso(
            @PathVariable Long id,
            @RequestBody PermisoSistemaRequest request) {
        return ResponseEntity.ok(permisoSistemaService.actualizarPermiso(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPermiso(@PathVariable Long id) {
        permisoSistemaService.eliminarPermiso(id);
        return ResponseEntity.noContent().build();
    }
}
