package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.ProductoProveedorRequest;
import DrinkGo.DrinkGo_backend.dto.ProductoProveedorResponse;
import DrinkGo.DrinkGo_backend.service.ProductoProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de Productos por Proveedor
 * RF-COM-001
 */
@RestController
@RequestMapping("/api/productos-proveedor")
@CrossOrigin(origins = "*")
public class ProductoProveedorController {

    @Autowired
    private ProductoProveedorService productoProveedorService;

    @GetMapping
    public ResponseEntity<List<ProductoProveedorResponse>> obtenerTodosLosProductosProveedor() {
        return ResponseEntity.ok(productoProveedorService.obtenerTodosLosProductosProveedor());
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<ProductoProveedorResponse>> obtenerProductosPorProveedor(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(productoProveedorService.obtenerProductosPorProveedor(proveedorId));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ProductoProveedorResponse>> obtenerProveedoresPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(productoProveedorService.obtenerProveedoresPorProducto(productoId));
    }

    @GetMapping("/producto/{productoId}/preferidos")
    public ResponseEntity<List<ProductoProveedorResponse>> obtenerProveedoresPreferidosPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(productoProveedorService.obtenerProveedoresPreferidosPorProducto(productoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoProveedorResponse> obtenerProductoProveedorPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoProveedorService.obtenerProductoProveedorPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProductoProveedorResponse> crearProductoProveedor(@RequestBody ProductoProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoProveedorService.crearProductoProveedor(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoProveedorResponse> actualizarProductoProveedor(
            @PathVariable Long id,
            @RequestBody ProductoProveedorRequest request) {
        return ResponseEntity.ok(productoProveedorService.actualizarProductoProveedor(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoProveedor(@PathVariable Long id) {
        productoProveedorService.eliminarProductoProveedor(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/proveedor/{proveedorId}")
    public ResponseEntity<Void> eliminarPorProveedor(@PathVariable Long proveedorId) {
        productoProveedorService.eliminarPorProveedor(proveedorId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/producto/{productoId}")
    public ResponseEntity<Void> eliminarPorProducto(@PathVariable Long productoId) {
        productoProveedorService.eliminarPorProducto(productoId);
        return ResponseEntity.noContent().build();
    }
}
