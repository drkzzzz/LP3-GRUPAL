package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.FacturaSuscripcionRequest;
import DrinkGo.DrinkGo_backend.dto.FacturaSuscripcionResponse;
import DrinkGo.DrinkGo_backend.service.FacturaSuscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de Facturas de Suscripción
 * RF-FAC-001, RF-FAC-002
 */
@RestController
@RequestMapping("/api/facturas-suscripcion")
@CrossOrigin(origins = "*")
public class FacturaSuscripcionController {

    @Autowired
    private FacturaSuscripcionService facturaSuscripcionService;

    @GetMapping
    public ResponseEntity<List<FacturaSuscripcionResponse>> obtenerTodasLasFacturas() {
        return ResponseEntity.ok(facturaSuscripcionService.obtenerTodasLasFacturas());
    }

    @GetMapping("/negocio/{negocioId}")
    public ResponseEntity<List<FacturaSuscripcionResponse>> obtenerFacturasPorNegocio(@PathVariable Long negocioId) {
        return ResponseEntity.ok(facturaSuscripcionService.obtenerFacturasPorNegocio(negocioId));
    }

    @GetMapping("/suscripcion/{suscripcionId}")
    public ResponseEntity<List<FacturaSuscripcionResponse>> obtenerFacturasPorSuscripcion(@PathVariable Long suscripcionId) {
        return ResponseEntity.ok(facturaSuscripcionService.obtenerFacturasPorSuscripcion(suscripcionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaSuscripcionResponse> obtenerFacturaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(facturaSuscripcionService.obtenerFacturaPorId(id));
    }

    @GetMapping("/numero/{numeroFactura}")
    public ResponseEntity<FacturaSuscripcionResponse> obtenerFacturaPorNumero(@PathVariable String numeroFactura) {
        return ResponseEntity.ok(facturaSuscripcionService.obtenerFacturaPorNumero(numeroFactura));
    }

    @PostMapping
    public ResponseEntity<FacturaSuscripcionResponse> crearFacturaSuscripcion(@RequestBody FacturaSuscripcionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facturaSuscripcionService.crearFacturaSuscripcion(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaSuscripcionResponse> actualizarFacturaSuscripcion(
            @PathVariable Long id,
            @RequestBody FacturaSuscripcionRequest request) {
        return ResponseEntity.ok(facturaSuscripcionService.actualizarFacturaSuscripcion(id, request));
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<FacturaSuscripcionResponse> marcarComoPagada(
            @PathVariable Long id,
            @RequestParam String metodoPago,
            @RequestParam(required = false) String referenciaPago) {
        return ResponseEntity.ok(facturaSuscripcionService.marcarComoPagada(id, metodoPago, referenciaPago));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long id) {
        facturaSuscripcionService.eliminarFactura(id);
        return ResponseEntity.noContent().build();
    }
}
