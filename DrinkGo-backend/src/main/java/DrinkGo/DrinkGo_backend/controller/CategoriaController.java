package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.Categoria;
import DrinkGo.DrinkGo_backend.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
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
        return 1L; // Modo desarrollo
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(categoriaService.findByNegocio(negocioId));
    }

    @GetMapping("/activas")
    public ResponseEntity<List<Categoria>> listarActivas() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(categoriaService.findActivas(negocioId));
    }

    @GetMapping("/principales")
    public ResponseEntity<List<Categoria>> listarPrincipales() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(categoriaService.findPrincipales(negocioId));
    }

    @GetMapping("/{id}/subcategorias")
    public ResponseEntity<List<Categoria>> listarSubcategorias(@PathVariable Long id) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(categoriaService.findSubcategorias(negocioId, id));
    }

    @GetMapping("/tienda-online")
    public ResponseEntity<List<Categoria>> listarVisiblesTiendaOnline() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(categoriaService.findVisiblesTiendaOnline(negocioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerPorId(@PathVariable Long id) {
        Optional<Categoria> categoria = categoriaService.findById(id);
        return categoria.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Categoria> obtenerPorSlug(@PathVariable String slug) {
        Long negocioId = obtenerNegocioId();
        Optional<Categoria> categoria = categoriaService.findBySlug(negocioId, slug);
        return categoria.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Categoria> crear(@RequestBody Categoria categoria) {
        Long negocioId = obtenerNegocioId();
        categoria.setNegocioId(negocioId);
        Categoria nuevaCategoria = categoriaService.crear(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id, @RequestBody Categoria categoria) {
        categoria.setId(id);
        Categoria categoriaActualizada = categoriaService.actualizar(categoria);
        return ResponseEntity.ok(categoriaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
