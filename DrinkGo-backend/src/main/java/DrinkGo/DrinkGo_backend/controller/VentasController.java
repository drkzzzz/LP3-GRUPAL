package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Ventas;
import DrinkGo.DrinkGo_backend.service.IVentasService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/restful")
public class VentasController {
    @Autowired
    private IVentasService service;

    @GetMapping("/ventas")
    public ResponseEntity<?> buscarTodos() {
        try {
            List<Ventas> ventas = service.buscarTodos();
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            System.out.println("ERROR EN GET VENTAS: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/ventas")
    public ResponseEntity<?> guardar(@RequestBody Ventas entity) {
        try {
            // Validar campos requeridos
            if (entity.getNegocio() == null || entity.getNegocio().getId() == null) {
                return ResponseEntity.badRequest().body("Error: El negocio es requerido");
            }
            if (entity.getSede() == null || entity.getSede().getId() == null) {
                return ResponseEntity.badRequest().body("Error: La sede es requerida");
            }
            if (entity.getUsuario() == null || entity.getUsuario().getId() == null) {
                return ResponseEntity.badRequest().body("Error: El usuario es requerido");
            }
            if (entity.getNumeroVenta() == null || entity.getNumeroVenta().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: El número de venta es requerido");
            }
            if (entity.getTipoVenta() == null) {
                return ResponseEntity.badRequest().body("Error: El tipo de venta es requerido (pos, tienda_online, mesa, telefono, otro)");
            }
            if (entity.getSubtotal() == null) {
                return ResponseEntity.badRequest().body("Error: El subtotal es requerido");
            }
            if (entity.getTotal() == null) {
                return ResponseEntity.badRequest().body("Error: El total es requerido");
            }

            // Establecer valores por defecto si no están definidos
            if (entity.getFechaVenta() == null) {
                entity.setFechaVenta(java.time.LocalDateTime.now());
            }
            if (entity.getEstado() == null) {
                entity.setEstado(Ventas.Estado.pendiente);
            }
            if (entity.getTipoComprobante() == null) {
                entity.setTipoComprobante(Ventas.TipoComprobante.boleta);
            }

            service.guardar(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(entity);
        } catch (Exception e) {
            System.out.println("ERROR EN POST VENTAS: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/ventas")
    public ResponseEntity<?> modificar(@RequestBody Ventas entity) {
        try {
            if (entity.getId() == null) {
                return ResponseEntity.badRequest().body("Error: El ID es requerido para actualizar");
            }
            service.modificar(entity);
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            System.out.println("ERROR EN PUT VENTAS: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/ventas/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Long id) {
        try {
            Optional<Ventas> venta = service.buscarId(id);
            return ResponseEntity.ok(venta);
        } catch (Exception e) {
            System.out.println("ERROR EN GET VENTAS/{ID}: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/ventas/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok("Venta eliminada correctamente");
        } catch (Exception e) {
            System.out.println("ERROR EN DELETE VENTAS/{ID}: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
