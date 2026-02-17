package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.NegocioRequest;
import DrinkGo.DrinkGo_backend.dto.NegocioResponse;
import DrinkGo.DrinkGo_backend.service.NegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de Negocios
 * RF-PLT-001, RF-ADM-001, RF-ADM-002
 */
@RestController
@RequestMapping("/api/negocios")
@CrossOrigin(origins = "*")
public class NegocioController {

    @Autowired
    private NegocioService negocioService;

    @GetMapping
    public ResponseEntity<List<NegocioResponse>> obtenerTodosLosNegocios() {
        return ResponseEntity.ok(negocioService.obtenerTodosLosNegocios());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<NegocioResponse>> obtenerNegociosActivos() {
        return ResponseEntity.ok(negocioService.obtenerNegociosActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NegocioResponse> obtenerNegocioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(negocioService.obtenerNegocioPorId(id));
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<NegocioResponse> obtenerNegocioPorUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(negocioService.obtenerNegocioPorUuid(uuid));
    }

    @PostMapping
    public ResponseEntity<NegocioResponse> crearNegocio(@RequestBody NegocioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(negocioService.crearNegocio(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NegocioResponse> actualizarNegocio(
            @PathVariable Long id,
            @RequestBody NegocioRequest request) {
        return ResponseEntity.ok(negocioService.actualizarNegocio(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNegocio(@PathVariable Long id) {
        negocioService.eliminarNegocio(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<NegocioResponse> cambiarEstadoNegocio(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(negocioService.cambiarEstadoNegocio(id, estado));
    }
}
