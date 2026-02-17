package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.Combo;
import DrinkGo.DrinkGo_backend.service.ComboService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/combos")
public class ComboController {

    private final ComboService comboService;

    public ComboController(ComboService comboService) {
        this.comboService = comboService;
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
    public ResponseEntity<List<Combo>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(comboService.findByNegocio(negocioId));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Combo>> listarActivos() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(comboService.findActivos(negocioId));
    }

    @GetMapping("/vigentes")
    public ResponseEntity<List<Combo>> listarVigentes() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(comboService.findVigentes(negocioId));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Combo>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(comboService.findByProducto(productoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Combo> obtenerPorId(@PathVariable Long id) {
        Optional<Combo> combo = comboService.findById(id);
        return combo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Combo> crear(@RequestBody Combo combo) {
        Long negocioId = obtenerNegocioId();
        combo.setNegocioId(negocioId);
        Combo nuevoCombo = comboService.crear(combo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCombo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Combo> actualizar(@PathVariable Long id, @RequestBody Combo combo) {
        combo.setId(id);
        Combo comboActualizado = comboService.actualizar(combo);
        return ResponseEntity.ok(comboActualizado);
    }

    @PutMapping("/{id}/incrementar-usos")
    public ResponseEntity<Combo> incrementarUsos(@PathVariable Long id) {
        Combo combo = comboService.incrementarUsos(id);
        if (combo != null) {
            return ResponseEntity.ok(combo);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        comboService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
