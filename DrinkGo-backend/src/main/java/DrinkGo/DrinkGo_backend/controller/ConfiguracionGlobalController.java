package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.ConfiguracionRequest;
import DrinkGo.DrinkGo_backend.entity.ConfiguracionGlobalPlataforma;
import DrinkGo.DrinkGo_backend.service.ConfiguracionGlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de Configuración Global - Bloque 1.
 * Gestiona configuraciones de plataforma (RF-CGL-001).
 */
@RestController
@RequestMapping("/restful/configuracion")
@CrossOrigin(origins = "*")
public class ConfiguracionGlobalController {

    @Autowired
    private ConfiguracionGlobalService configuracionService;

    /**
     * GET /restful/configuracion - Listar todas las configuraciones (SuperAdmin).
     */
    @GetMapping
    public ResponseEntity<List<ConfiguracionGlobalPlataforma>> listarConfiguraciones() {
        List<ConfiguracionGlobalPlataforma> configuraciones = configuracionService.listarConfiguraciones();
        return ResponseEntity.ok(configuraciones);
    }

    /**
     * GET /restful/configuracion/{id} - Obtener configuración por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            ConfiguracionGlobalPlataforma config = configuracionService.obtenerPorId(id);
            return ResponseEntity.ok(config);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * GET /restful/configuracion/clave/{clave} - Obtener configuración por clave.
     */
    @GetMapping("/clave/{clave}")
    public ResponseEntity<?> obtenerPorClave(@PathVariable String clave) {
        try {
            ConfiguracionGlobalPlataforma config = configuracionService.obtenerPorClave(clave);
            return ResponseEntity.ok(config);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * POST /restful/configuracion - Crear nueva configuración (SuperAdmin) (RF-CGL-001).
     */
    @PostMapping
    public ResponseEntity<?> crearConfiguracion(@RequestBody ConfiguracionRequest request) {
        try {
            ConfiguracionGlobalPlataforma nuevaConfig = configuracionService.crearConfiguracion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaConfig);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * PUT /restful/configuracion/{id} - Actualizar configuración (SuperAdmin).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarConfiguracion(
            @PathVariable Long id,
            @RequestBody ConfiguracionRequest request) {
        try {
            ConfiguracionGlobalPlataforma configActualizada = 
                configuracionService.actualizarConfiguracion(id, request);
            return ResponseEntity.ok(configActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * DELETE /restful/configuracion/{id} - Eliminar configuración (SuperAdmin).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarConfiguracion(@PathVariable Long id) {
        try {
            configuracionService.eliminarConfiguracion(id);
            return ResponseEntity.ok("Configuración eliminada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
