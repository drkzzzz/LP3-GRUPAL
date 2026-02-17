package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.DetalleRecepcionCompraRequest;
import DrinkGo.DrinkGo_backend.dto.DetalleRecepcionCompraResponse;
import DrinkGo.DrinkGo_backend.service.DetalleRecepcionCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de Detalles de Recepciones de Compra
 * RF-COM-004 a RF-COM-007
 */
@RestController
@RequestMapping("/api/detalles-recepcion-compra")
@CrossOrigin(origins = "*")
public class DetalleRecepcionCompraController {

    @Autowired
    private DetalleRecepcionCompraService detalleRecepcionCompraService;

    @GetMapping
    public ResponseEntity<List<DetalleRecepcionCompraResponse>> obtenerTodosLosDetalles() {
        return ResponseEntity.ok(detalleRecepcionCompraService.obtenerTodosLosDetalles());
    }

    @GetMapping("/recepcion/{recepcionId}")
    public ResponseEntity<List<DetalleRecepcionCompraResponse>> obtenerDetallesPorRecepcion(@PathVariable Long recepcionId) {
        return ResponseEntity.ok(detalleRecepcionCompraService.obtenerDetallesPorRecepcion(recepcionId));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<DetalleRecepcionCompraResponse>> obtenerDetallesPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(detalleRecepcionCompraService.obtenerDetallesPorProducto(productoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleRecepcionCompraResponse> obtenerDetallePorId(@PathVariable Long id) {
        return ResponseEntity.ok(detalleRecepcionCompraService.obtenerDetallePorId(id));
    }

    @PostMapping
    public ResponseEntity<DetalleRecepcionCompraResponse> crearDetalle(@RequestBody DetalleRecepcionCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detalleRecepcionCompraService.crearDetalle(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleRecepcionCompraResponse> actualizarDetalle(
            @PathVariable Long id,
            @RequestBody DetalleRecepcionCompraRequest request) {
        return ResponseEntity.ok(detalleRecepcionCompraService.actualizarDetalle(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long id) {
        detalleRecepcionCompraService.eliminarDetalle(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/recepcion/{recepcionId}")
    public ResponseEntity<Void> eliminarDetallesPorRecepcion(@PathVariable Long recepcionId) {
        detalleRecepcionCompraService.eliminarDetallesPorRecepcion(recepcionId);
        return ResponseEntity.noContent().build();
    }
}
