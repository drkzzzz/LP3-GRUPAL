package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.ProductoSede;
import DrinkGo.DrinkGo_backend.service.ProductoSedeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos-sedes")
public class ProductoSedeController {

    private final ProductoSedeService productoSedeService;

    public ProductoSedeController(ProductoSedeService productoSedeService) {
        this.productoSedeService = productoSedeService;
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ProductoSede>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(productoSedeService.findByProducto(productoId));
    }

    @GetMapping("/sede/{sedeId}")
    public ResponseEntity<List<ProductoSede>> listarPorSede(@PathVariable Long sedeId) {
        return ResponseEntity.ok(productoSedeService.findBySede(sedeId));
    }

    @GetMapping("/sede/{sedeId}/disponibles")
    public ResponseEntity<List<ProductoSede>> listarDisponiblesPorSede(@PathVariable Long sedeId) {
        return ResponseEntity.ok(productoSedeService.findDisponiblesBySede(sedeId));
    }

    @GetMapping("/producto/{productoId}/sede/{sedeId}")
    public ResponseEntity<ProductoSede> obtenerPorProductoYSede(@PathVariable Long productoId,
            @PathVariable Long sedeId) {
        Optional<ProductoSede> productoSede = productoSedeService.findByProductoAndSede(productoId, sedeId);
        return productoSede.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoSede> obtenerPorId(@PathVariable Long id) {
        Optional<ProductoSede> productoSede = productoSedeService.findById(id);
        return productoSede.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductoSede> crear(@RequestBody ProductoSede productoSede) {
        ProductoSede nuevoProductoSede = productoSedeService.crear(productoSede);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProductoSede);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoSede> actualizar(@PathVariable Long id, @RequestBody ProductoSede productoSede) {
        productoSede.setId(id);
        ProductoSede productoSedeActualizado = productoSedeService.actualizar(productoSede);
        return ResponseEntity.ok(productoSedeActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoSedeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
