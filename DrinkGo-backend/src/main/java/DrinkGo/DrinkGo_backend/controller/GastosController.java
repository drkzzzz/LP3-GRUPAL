package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Gastos;
import DrinkGo.DrinkGo_backend.service.IGastosService;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/restful")
public class GastosController {
    @Autowired
    private IGastosService service;

    @GetMapping("/gastos")
    public List<Gastos> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/gastos/negocio/{negocioId}")
    public List<Gastos> buscarPorNegocio(@PathVariable("negocioId") Long negocioId) {
        return service.buscarPorNegocio(negocioId);
    }

    @PostMapping("/gastos")
    public ResponseEntity<?> guardar(@RequestBody Gastos entity) {
        if (entity.getMonto() != null && entity.getMonto().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", "El monto debe ser mayor a cero"));
        }
        return ResponseEntity.ok(service.guardar(entity));
    }

    @PutMapping("/gastos")
    public ResponseEntity<?> modificar(@RequestBody Gastos entity) {
        if (entity.getMonto() != null && entity.getMonto().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", "El monto debe ser mayor a cero"));
        }
        return ResponseEntity.ok(service.modificar(entity));
    }

    @GetMapping("/gastos/{id}")
    public Optional<Gastos> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/gastos/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }

    @PatchMapping("/gastos/{id}/pagar")
    public ResponseEntity<?> marcarPagado(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) String referencia) {
        try {
            Gastos gasto = service.marcarPagado(id, metodoPago, referencia);
            return ResponseEntity.ok(gasto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/gastos/{id}/comprobante", consumes = "multipart/form-data")
    public ResponseEntity<?> subirComprobante(
            @PathVariable("id") Long id,
            @RequestPart("archivo") MultipartFile archivo,
            @RequestParam(value = "metodoPago", required = false) String metodoPago,
            @RequestParam(value = "referenciaPago", required = false) String referenciaPago) {
        try {
            // Validar tamaño (máx 5 MB)
            if (archivo.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(java.util.Map.of("error", "El archivo supera el límite de 5 MB"));
            }
            // Validar tipo MIME
            String contentType = archivo.getContentType();
            if (contentType == null || !(
                    contentType.equals("image/jpeg") ||
                    contentType.equals("image/png") ||
                    contentType.equals("image/webp") ||
                    contentType.equals("application/pdf"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(java.util.Map.of("error", "Tipo de archivo no permitido. Use JPG, PNG, WebP o PDF"));
            }
            Gastos gasto = service.subirComprobante(id, archivo, metodoPago, referenciaPago);
            return ResponseEntity.ok(java.util.Map.of(
                    "message", "Comprobante subido correctamente",
                    "urlComprobante", gasto.getUrlComprobante() != null ? gasto.getUrlComprobante() : ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/gastos/{id}/comprobante")
    public ResponseEntity<?> eliminarComprobante(@PathVariable("id") Long id) {
        try {
            Gastos gasto = service.eliminarComprobante(id);
            return ResponseEntity.ok(gasto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
