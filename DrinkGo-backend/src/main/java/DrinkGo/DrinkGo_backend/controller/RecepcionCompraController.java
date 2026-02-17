package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.RecepcionCompraRequest;
import DrinkGo.DrinkGo_backend.dto.RecepcionCompraResponse;
import DrinkGo.DrinkGo_backend.service.RecepcionCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller para gestión de Recepciones de Compra
 * RF-COM-004 a RF-COM-007
 */
@RestController
@RequestMapping("/api/recepciones-compra")
@CrossOrigin(origins = "*")
public class RecepcionCompraController {

    @Autowired
    private RecepcionCompraService recepcionCompraService;

    @GetMapping
    public ResponseEntity<List<RecepcionCompraResponse>> obtenerTodasLasRecepciones() {
        return ResponseEntity.ok(recepcionCompraService.obtenerTodasLasRecepciones());
    }

    @GetMapping("/negocio/{negocioId}")
    public ResponseEntity<List<RecepcionCompraResponse>> obtenerRecepcionesPorNegocio(@PathVariable Long negocioId) {
        return ResponseEntity.ok(recepcionCompraService.obtenerRecepcionesPorNegocio(negocioId));
    }

    @GetMapping("/orden/{ordenCompraId}")
    public ResponseEntity<List<RecepcionCompraResponse>> obtenerRecepcionesPorOrden(@PathVariable Long ordenCompraId) {
        return ResponseEntity.ok(recepcionCompraService.obtenerRecepcionesPorOrden(ordenCompraId));
    }

    @GetMapping("/negocio/{negocioId}/fecha")
    public ResponseEntity<List<RecepcionCompraResponse>> obtenerRecepcionesPorFecha(
            @PathVariable Long negocioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(recepcionCompraService.obtenerRecepcionesPorFecha(negocioId, fechaInicio, fechaFin));
    }

    @GetMapping("/negocio/{negocioId}/numero/{numeroRecepcion}")
    public ResponseEntity<RecepcionCompraResponse> obtenerRecepcionPorNumero(
            @PathVariable Long negocioId,
            @PathVariable String numeroRecepcion) {
        return ResponseEntity.ok(recepcionCompraService.obtenerRecepcionPorNumero(negocioId, numeroRecepcion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecepcionCompraResponse> obtenerRecepcionPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recepcionCompraService.obtenerRecepcionPorId(id));
    }

    @PostMapping
    public ResponseEntity<RecepcionCompraResponse> crearRecepcion(@RequestBody RecepcionCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recepcionCompraService.crearRecepcion(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecepcionCompraResponse> actualizarRecepcion(
            @PathVariable Long id,
            @RequestBody RecepcionCompraRequest request) {
        return ResponseEntity.ok(recepcionCompraService.actualizarRecepcion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRecepcion(@PathVariable Long id) {
        recepcionCompraService.eliminarRecepcion(id);
        return ResponseEntity.noContent().build();
    }
}
