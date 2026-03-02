package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.FacturasSuscripcion;
import DrinkGo.DrinkGo_backend.service.IFacturasSuscripcionService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/restful")
public class FacturasSuscripcionController {
    @Autowired
    private IFacturasSuscripcionService service;

    @GetMapping("/facturas-suscripcion")
    public List<FacturasSuscripcion> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/facturas-suscripcion/por-negocio/{negocioId}")
    public List<FacturasSuscripcion> buscarPorNegocio(@PathVariable("negocioId") Long negocioId) {
        return service.buscarPorNegocio(negocioId);
    }

    @PostMapping("/facturas-suscripcion")
    public FacturasSuscripcion guardar(@RequestBody FacturasSuscripcion entity) {
        service.guardar(entity);
        return entity;
    }

    @Autowired
    private DrinkGo.DrinkGo_backend.service.IRegistrosAuditoriaService auditoriaService;

    /** Genera automáticamente una factura para una suscripción (cargo mensual) */
    @PostMapping("/facturas-suscripcion/generar/{suscripcionId}")
    public ResponseEntity<?> generar(@PathVariable("suscripcionId") Long suscripcionId) {
        try {
            FacturasSuscripcion factura = service.generarFactura(suscripcionId);
            try {
                Long negocioId = factura.getNegocio() != null ? factura.getNegocio().getId() : null;
                auditoriaService.registrar(
                        "FACTURA_GENERADA",
                        "FacturasSuscripcion",
                        factura.getId(),
                        "Factura generada: " + factura.getNumeroFactura(),
                        DrinkGo.DrinkGo_backend.entity.RegistrosAuditoria.Severidad.info,
                        "superadmin",
                        negocioId);
            } catch (Exception ignored) {
            }
            return ResponseEntity.ok(factura);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /** Marca una factura como pagada */
    @PatchMapping("/facturas-suscripcion/{id}/pagar")
    public ResponseEntity<?> pagar(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) String referencia) {
        try {
            FacturasSuscripcion factura = service.marcarPagada(id, metodoPago, referencia);
            try {
                Long negocioId = factura.getNegocio() != null ? factura.getNegocio().getId() : null;
                auditoriaService.registrar(
                        "FACTURA_PAGADA",
                        "FacturasSuscripcion",
                        factura.getId(),
                        "Factura pagada: " + factura.getNumeroFactura()
                                + (metodoPago != null ? " vía " + metodoPago : ""),
                        DrinkGo.DrinkGo_backend.entity.RegistrosAuditoria.Severidad.info,
                        "superadmin",
                        negocioId);
            } catch (Exception ignored) {
            }
            return ResponseEntity.ok(factura);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /** Anula/cancela una factura pendiente */
    @PatchMapping("/facturas-suscripcion/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable("id") Long id) {
        try {
            FacturasSuscripcion factura = service.cancelarFactura(id);
            try {
                Long negocioId = factura.getNegocio() != null ? factura.getNegocio().getId() : null;
                auditoriaService.registrar(
                        "FACTURA_ANULADA",
                        "FacturasSuscripcion",
                        factura.getId(),
                        "Factura anulada: " + factura.getNumeroFactura(),
                        DrinkGo.DrinkGo_backend.entity.RegistrosAuditoria.Severidad.advertencia,
                        "superadmin",
                        negocioId);
            } catch (Exception ignored) {
            }
            return ResponseEntity.ok(factura);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/facturas-suscripcion")
    public FacturasSuscripcion modificar(@RequestBody FacturasSuscripcion entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/facturas-suscripcion/{id}")
    public Optional<FacturasSuscripcion> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/facturas-suscripcion/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
