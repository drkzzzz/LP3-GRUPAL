package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.Marca;
import DrinkGo.DrinkGo_backend.service.MarcaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    private final MarcaService marcaService;

    public MarcaController(MarcaService marcaService) {
        this.marcaService = marcaService;
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
    public ResponseEntity<List<Marca>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(marcaService.findByNegocio(negocioId));
    }

    @GetMapping("/activas")
    public ResponseEntity<List<Marca>> listarActivas() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(marcaService.findActivas(negocioId));
    }

    @GetMapping("/pais/{paisOrigen}")
    public ResponseEntity<List<Marca>> listarPorPais(@PathVariable String paisOrigen) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(marcaService.findByPaisOrigen(negocioId, paisOrigen));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marca> obtenerPorId(@PathVariable Long id) {
        Optional<Marca> marca = marcaService.findById(id);
        return marca.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Marca> obtenerPorSlug(@PathVariable String slug) {
        Long negocioId = obtenerNegocioId();
        Optional<Marca> marca = marcaService.findBySlug(negocioId, slug);
        return marca.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Marca> crear(@RequestBody Marca marca) {
        Long negocioId = obtenerNegocioId();
        marca.setNegocioId(negocioId);
        Marca nuevaMarca = marcaService.crear(marca);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMarca);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Marca> actualizar(@PathVariable Long id, @RequestBody Marca marca) {
        marca.setId(id);
        Marca marcaActualizada = marcaService.actualizar(marca);
        return ResponseEntity.ok(marcaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        marcaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
