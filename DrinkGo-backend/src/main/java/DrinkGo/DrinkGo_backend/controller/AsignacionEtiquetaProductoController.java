package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.AsignacionEtiquetaProducto;
import DrinkGo.DrinkGo_backend.service.AsignacionEtiquetaProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asignacion-etiquetas")
public class AsignacionEtiquetaProductoController {

    private final AsignacionEtiquetaProductoService asignacionService;

    public AsignacionEtiquetaProductoController(AsignacionEtiquetaProductoService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<AsignacionEtiquetaProducto>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(asignacionService.findByProducto(productoId));
    }

    @GetMapping("/etiqueta/{etiquetaId}")
    public ResponseEntity<List<AsignacionEtiquetaProducto>> listarPorEtiqueta(@PathVariable Long etiquetaId) {
        return ResponseEntity.ok(asignacionService.findByEtiqueta(etiquetaId));
    }

    @PostMapping
    public ResponseEntity<AsignacionEtiquetaProducto> asignar(@RequestBody Map<String, Long> request) {
        Long productoId = request.get("productoId");
        Long etiquetaId = request.get("etiquetaId");
        AsignacionEtiquetaProducto asignacion = asignacionService.asignar(productoId, etiquetaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(asignacion);
    }

    @DeleteMapping("/producto/{productoId}/etiqueta/{etiquetaId}")
    public ResponseEntity<Map<String, String>> desasignar(@PathVariable Long productoId,
            @PathVariable Long etiquetaId) {
        asignacionService.desasignar(productoId, etiquetaId);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Etiqueta desasignada correctamente");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/producto/{productoId}")
    public ResponseEntity<Void> eliminarPorProducto(@PathVariable Long productoId) {
        asignacionService.eliminarPorProducto(productoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/etiqueta/{etiquetaId}")
    public ResponseEntity<Void> eliminarPorEtiqueta(@PathVariable Long etiquetaId) {
        asignacionService.eliminarPorEtiqueta(etiquetaId);
        return ResponseEntity.noContent().build();
    }
}
