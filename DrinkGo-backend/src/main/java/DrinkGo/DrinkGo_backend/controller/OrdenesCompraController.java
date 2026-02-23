package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.OrdenesCompra;
import DrinkGo.DrinkGo_backend.service.IOrdenesCompraService;
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
public class OrdenesCompraController {
    @Autowired
    private IOrdenesCompraService service;

    @GetMapping("/ordenes-compra")
    public ResponseEntity<?> buscarTodos() {
        try {
            List<OrdenesCompra> ordenes = service.buscarTodos();
            return ResponseEntity.ok(ordenes);
        } catch (Exception e) {
            System.out.println("ERROR EN GET ORDENES-COMPRA: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/ordenes-compra")
    public ResponseEntity<?> guardar(@RequestBody OrdenesCompra entity) {
        try {
            // Validar campos requeridos según la BD
            if (entity.getNegocio() == null || entity.getNegocio().getId() == null) {
                return ResponseEntity.badRequest().body("Error: El negocio es requerido");
            }
            if (entity.getNumeroOrden() == null || entity.getNumeroOrden().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: El número de orden es requerido");
            }
            if (entity.getProveedor() == null || entity.getProveedor().getId() == null) {
                return ResponseEntity.badRequest().body("Error: El proveedor es requerido");
            }
            if (entity.getSede() == null || entity.getSede().getId() == null) {
                return ResponseEntity.badRequest().body("Error: La sede es requerida");
            }
            if (entity.getAlmacen() == null || entity.getAlmacen().getId() == null) {
                return ResponseEntity.badRequest().body("Error: El almacén es requerido");
            }
            if (entity.getSubtotal() == null) {
                return ResponseEntity.badRequest().body("Error: El subtotal es requerido");
            }
            if (entity.getTotal() == null) {
                return ResponseEntity.badRequest().body("Error: El total es requerido");
            }
            if (entity.getUsuario() == null || entity.getUsuario().getId() == null) {
                return ResponseEntity.badRequest().body("Error: El usuario es requerido");
            }

            // Establecer valores por defecto si no están definidos
            if (entity.getEstado() == null) {
                entity.setEstado(OrdenesCompra.Estado.pendiente);
            }

            service.guardar(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(entity);
        } catch (Exception e) {
            System.out.println("ERROR EN POST ORDENES-COMPRA: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/ordenes-compra")
    public ResponseEntity<?> modificar(@RequestBody OrdenesCompra entity) {
        try {
            if (entity.getId() == null) {
                return ResponseEntity.badRequest().body("Error: El ID es requerido para actualizar");
            }
            service.modificar(entity);
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            System.out.println("ERROR EN PUT ORDENES-COMPRA: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/ordenes-compra/{id}")
    public ResponseEntity<?> buscarId(@PathVariable("id") Long id) {
        try {
            Optional<OrdenesCompra> orden = service.buscarId(id);
            return ResponseEntity.ok(orden);
        } catch (Exception e) {
            System.out.println("ERROR EN GET ORDENES-COMPRA/{ID}: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/ordenes-compra/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok("Orden de compra eliminada correctamente");
        } catch (Exception e) {
            System.out.println("ERROR EN DELETE ORDENES-COMPRA/{ID}: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
