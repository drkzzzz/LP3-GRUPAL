package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.DetalleCombo;
import DrinkGo.DrinkGo_backend.service.DetalleComboService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/detalle-combos")
public class DetalleComboController {

    private final DetalleComboService detalleComboService;

    public DetalleComboController(DetalleComboService detalleComboService) {
        this.detalleComboService = detalleComboService;
    }

    @GetMapping("/combo/{comboId}")
    public ResponseEntity<List<DetalleCombo>> listarPorCombo(@PathVariable Long comboId) {
        return ResponseEntity.ok(detalleComboService.findByCombo(comboId));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<DetalleCombo>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(detalleComboService.findByProducto(productoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleCombo> obtenerPorId(@PathVariable Long id) {
        Optional<DetalleCombo> detalle = detalleComboService.findById(id);
        return detalle.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DetalleCombo> crear(@RequestBody DetalleCombo detalleCombo) {
        DetalleCombo nuevoDetalle = detalleComboService.crear(detalleCombo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDetalle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleCombo> actualizar(@PathVariable Long id, @RequestBody DetalleCombo detalleCombo) {
        detalleCombo.setId(id);
        DetalleCombo detalleActualizado = detalleComboService.actualizar(detalleCombo);
        return ResponseEntity.ok(detalleActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        detalleComboService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/combo/{comboId}")
    public ResponseEntity<Void> eliminarPorCombo(@PathVariable Long comboId) {
        detalleComboService.eliminarPorCombo(comboId);
        return ResponseEntity.noContent().build();
    }
}
