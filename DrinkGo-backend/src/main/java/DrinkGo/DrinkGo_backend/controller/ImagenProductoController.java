package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.ImagenProducto;
import DrinkGo.DrinkGo_backend.service.ImagenProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/imagenes-producto")
public class ImagenProductoController {

    private final ImagenProductoService imagenProductoService;

    public ImagenProductoController(ImagenProductoService imagenProductoService) {
        this.imagenProductoService = imagenProductoService;
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ImagenProducto>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(imagenProductoService.findByProducto(productoId));
    }

    @GetMapping("/producto/{productoId}/principal")
    public ResponseEntity<ImagenProducto> obtenerImagenPrincipal(@PathVariable Long productoId) {
        Optional<ImagenProducto> imagen = imagenProductoService.findImagenPrincipal(productoId);
        return imagen.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImagenProducto> obtenerPorId(@PathVariable Long id) {
        Optional<ImagenProducto> imagen = imagenProductoService.findById(id);
        return imagen.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ImagenProducto> crear(@RequestBody ImagenProducto imagenProducto) {
        ImagenProducto nuevaImagen = imagenProductoService.crear(imagenProducto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaImagen);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImagenProducto> actualizar(@PathVariable Long id,
            @RequestBody ImagenProducto imagenProducto) {
        imagenProducto.setId(id);
        ImagenProducto imagenActualizada = imagenProductoService.actualizar(imagenProducto);
        return ResponseEntity.ok(imagenActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        imagenProductoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/producto/{productoId}")
    public ResponseEntity<Void> eliminarPorProducto(@PathVariable Long productoId) {
        imagenProductoService.eliminarPorProducto(productoId);
        return ResponseEntity.noContent().build();
    }
}
