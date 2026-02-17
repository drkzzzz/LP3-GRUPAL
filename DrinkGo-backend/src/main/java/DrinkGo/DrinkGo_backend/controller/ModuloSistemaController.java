package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.ModuloSistemaRequest;
import DrinkGo.DrinkGo_backend.dto.ModuloSistemaResponse;
import DrinkGo.DrinkGo_backend.service.ModuloSistemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de Módulos del Sistema
 * RF-ADM-017
 */
@RestController
@RequestMapping("/api/modulos-sistema")
@CrossOrigin(origins = "*")
public class ModuloSistemaController {

    @Autowired
    private ModuloSistemaService moduloSistemaService;

    @GetMapping
    public ResponseEntity<List<ModuloSistemaResponse>> obtenerTodosLosModulos() {
        return ResponseEntity.ok(moduloSistemaService.obtenerTodosLosModulos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ModuloSistemaResponse>> obtenerModulosActivos() {
        return ResponseEntity.ok(moduloSistemaService.obtenerModulosActivos());
    }

    @GetMapping("/principales")
    public ResponseEntity<List<ModuloSistemaResponse>> obtenerModulosPrincipales() {
        return ResponseEntity.ok(moduloSistemaService.obtenerModulosPrincipales());
    }

    @GetMapping("/submodulos/{moduloPadreId}")
    public ResponseEntity<List<ModuloSistemaResponse>> obtenerSubmodulos(@PathVariable Long moduloPadreId) {
        return ResponseEntity.ok(moduloSistemaService.obtenerSubmodulos(moduloPadreId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuloSistemaResponse> obtenerModuloPorId(@PathVariable Long id) {
        return ResponseEntity.ok(moduloSistemaService.obtenerModuloPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ModuloSistemaResponse> obtenerModuloPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(moduloSistemaService.obtenerModuloPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<ModuloSistemaResponse> crearModulo(@RequestBody ModuloSistemaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(moduloSistemaService.crearModulo(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuloSistemaResponse> actualizarModulo(
            @PathVariable Long id,
            @RequestBody ModuloSistemaRequest request) {
        return ResponseEntity.ok(moduloSistemaService.actualizarModulo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarModulo(@PathVariable Long id) {
        moduloSistemaService.eliminarModulo(id);
        return ResponseEntity.noContent().build();
    }
}
