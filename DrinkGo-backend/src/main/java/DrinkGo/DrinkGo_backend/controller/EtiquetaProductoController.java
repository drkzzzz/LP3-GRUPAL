package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.EtiquetaProducto;
import DrinkGo.DrinkGo_backend.service.EtiquetaProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/etiquetas-producto")
public class EtiquetaProductoController {

    private final EtiquetaProductoService etiquetaProductoService;

    public EtiquetaProductoController(EtiquetaProductoService etiquetaProductoService) {
        this.etiquetaProductoService = etiquetaProductoService;
    }

    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) auth.getDetails();
            Object negocioId = details.get("negocioId");
            if (negocioId != null) {
                return Long.valueOf(negocioId.toString());
            }
        }
        return 1L;
    }

    @GetMapping
    public ResponseEntity<List<EtiquetaProducto>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(etiquetaProductoService.findByNegocio(negocioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EtiquetaProducto> obtenerPorId(@PathVariable Long id) {
        Optional<EtiquetaProducto> etiqueta = etiquetaProductoService.findById(id);
        return etiqueta.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<EtiquetaProducto> obtenerPorSlug(@PathVariable String slug) {
        Long negocioId = obtenerNegocioId();
        Optional<EtiquetaProducto> etiqueta = etiquetaProductoService.findBySlug(negocioId, slug);
        return etiqueta.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EtiquetaProducto> crear(@RequestBody EtiquetaProducto etiquetaProducto) {
        Long negocioId = obtenerNegocioId();
        etiquetaProducto.setNegocioId(negocioId);
        EtiquetaProducto nuevaEtiqueta = etiquetaProductoService.crear(etiquetaProducto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEtiqueta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EtiquetaProducto> actualizar(@PathVariable Long id,
            @RequestBody EtiquetaProducto etiquetaProducto) {
        etiquetaProducto.setId(id);
        EtiquetaProducto etiquetaActualizada = etiquetaProductoService.actualizar(etiquetaProducto);
        return ResponseEntity.ok(etiquetaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        etiquetaProductoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
