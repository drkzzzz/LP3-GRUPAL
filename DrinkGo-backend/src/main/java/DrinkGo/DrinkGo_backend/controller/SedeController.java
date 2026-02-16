package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.SedeDTO;
import DrinkGo.DrinkGo_backend.entity.Sede;
import DrinkGo.DrinkGo_backend.entity.SedeConfig;
import DrinkGo.DrinkGo_backend.service.SedeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de Sedes (V4)
 */
@RestController
@RequestMapping("/api/sedes")
public class SedeController {
    
    private final SedeService sedeService;
    
    public SedeController(SedeService sedeService) {
        this.sedeService = sedeService;
    }
    
    @GetMapping
    public ResponseEntity<List<Sede>> listar(@RequestParam Long tenantId) {
        return ResponseEntity.ok(sedeService.findByTenant(tenantId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Sede> obtener(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseEntity.ok(sedeService.findById(id, tenantId));
    }
    
    @GetMapping("/con-mesas")
    public ResponseEntity<List<Sede>> listarConMesas(@RequestParam Long tenantId) {
        return ResponseEntity.ok(sedeService.findSedesConMesas(tenantId));
    }
    
    @GetMapping("/con-delivery")
    public ResponseEntity<List<Sede>> listarConDelivery(@RequestParam Long tenantId) {
        return ResponseEntity.ok(sedeService.findSedesConDelivery(tenantId));
    }
    
    @PostMapping
    public ResponseEntity<Sede> crear(@RequestBody SedeDTO dto, @RequestParam Long tenantId) {
        Sede sede = sedeService.crear(dto, tenantId);
        return ResponseEntity.ok(sede);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Sede> actualizar(@PathVariable Long id, @RequestBody SedeDTO dto, 
                                           @RequestParam Long tenantId) {
        Sede sede = sedeService.actualizar(id, dto, tenantId);
        return ResponseEntity.ok(sede);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id, @RequestParam Long tenantId) {
        sedeService.desactivar(id, tenantId);
        return ResponseEntity.noContent().build();
    }
    
    // Configuración de sede
    @GetMapping("/{sedeId}/config")
    public ResponseEntity<SedeConfig> obtenerConfiguracion(@PathVariable Long sedeId) {
        return ResponseEntity.ok(sedeService.getConfiguracion(sedeId));
    }
    
    @PutMapping("/{sedeId}/config")
    public ResponseEntity<SedeConfig> actualizarConfiguracion(@PathVariable Long sedeId,
                                                               @RequestBody SedeConfig config) {
        return ResponseEntity.ok(sedeService.actualizarConfiguracion(sedeId, config));
    }
}
