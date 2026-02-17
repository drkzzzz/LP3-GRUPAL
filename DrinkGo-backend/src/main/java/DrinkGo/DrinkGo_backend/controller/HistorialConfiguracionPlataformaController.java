package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.HistorialConfiguracionPlataformaRequest;
import DrinkGo.DrinkGo_backend.dto.HistorialConfiguracionPlataformaResponse;
import DrinkGo.DrinkGo_backend.service.HistorialConfiguracionPlataformaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de Historial de Configuración de Plataforma
 * RF-CGL-001
 */
@RestController
@RequestMapping("/api/historial-configuracion-plataforma")
@CrossOrigin(origins = "*")
public class HistorialConfiguracionPlataformaController {

    @Autowired
    private HistorialConfiguracionPlataformaService historialConfiguracionPlataformaService;

    @GetMapping
    public ResponseEntity<List<HistorialConfiguracionPlataformaResponse>> obtenerTodoElHistorial() {
        return ResponseEntity.ok(historialConfiguracionPlataformaService.obtenerTodoElHistorial());
    }

    @GetMapping("/configuracion/{configuracionId}")
    public ResponseEntity<List<HistorialConfiguracionPlataformaResponse>> obtenerHistorialPorConfiguracion(
            @PathVariable Long configuracionId) {
        return ResponseEntity.ok(historialConfiguracionPlataformaService.obtenerHistorialPorConfiguracion(configuracionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialConfiguracionPlataformaResponse> obtenerHistorialPorId(@PathVariable Long id) {
        return ResponseEntity.ok(historialConfiguracionPlataformaService.obtenerHistorialPorId(id));
    }

    @PostMapping
    public ResponseEntity<HistorialConfiguracionPlataformaResponse> crearHistorial(
            @RequestBody HistorialConfiguracionPlataformaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(historialConfiguracionPlataformaService.crearHistorial(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistorialConfiguracionPlataformaResponse> actualizarHistorial(
            @PathVariable Long id,
            @RequestBody HistorialConfiguracionPlataformaRequest request) {
        return ResponseEntity.ok(historialConfiguracionPlataformaService.actualizarHistorial(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHistorial(@PathVariable Long id) {
        historialConfiguracionPlataformaService.eliminarHistorial(id);
        return ResponseEntity.noContent().build();
    }
}
