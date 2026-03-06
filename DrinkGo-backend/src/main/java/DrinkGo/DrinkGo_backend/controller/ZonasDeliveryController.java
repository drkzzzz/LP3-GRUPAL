package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.ZonasDelivery;
import DrinkGo.DrinkGo_backend.service.IZonasDeliveryService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/restful")
public class ZonasDeliveryController {
    @Autowired
    private IZonasDeliveryService service;

    @GetMapping("/zonas-delivery")
    public List<ZonasDelivery> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/zonas-delivery")
    public ZonasDelivery guardar(@RequestBody ZonasDelivery entity) {
        System.out.println("🔵 [POST /zonas-delivery] Recibido:");
        System.out.println("  - Nombre: " + entity.getNombre());
        System.out.println("  - Distritos (raw): " + entity.getDistritos());
        System.out.println("  - Tarifa: " + entity.getTarifaDelivery());
        System.out.println("  - Monto mínimo: " + entity.getMontoMinimoPedido());
        System.out.println("  - Negocio ID: " + (entity.getNegocio() != null ? entity.getNegocio().getId() : "NULL"));
        System.out.println("  - Sede ID: " + (entity.getSede() != null ? entity.getSede().getId() : "NULL"));
        
        service.guardar(entity);
        
        System.out.println("✅ Zona guardada con ID: " + entity.getId());
        return entity;
    }

    @PutMapping("/zonas-delivery")
    public ZonasDelivery modificar(@RequestBody ZonasDelivery entity) {
        System.out.println("🔵 [PUT /zonas-delivery] Recibido:");
        System.out.println("  - ID: " + entity.getId());
        System.out.println("  - Nombre: " + entity.getNombre());
        System.out.println("  - Distritos (raw): " + entity.getDistritos());
        System.out.println("  - Tarifa: " + entity.getTarifaDelivery());
        
        service.modificar(entity);
        
        System.out.println("✅ Zona actualizada");
        return entity;
    }

    @GetMapping("/zonas-delivery/{id}")
    public Optional<ZonasDelivery> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/zonas-delivery/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
