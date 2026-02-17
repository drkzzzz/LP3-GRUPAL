package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.AlmacenDTO;
import DrinkGo.DrinkGo_backend.entity.Almacen;
import DrinkGo.DrinkGo_backend.service.AlmacenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestión de Almacenes (V4)
 */
@RestController
@RequestMapping("/api/almacenes")
public class AlmacenController {
    
    private final AlmacenService almacenService;
    
    public AlmacenController(AlmacenService almacenService) {
        this.almacenService = almacenService;
    }
    
    @GetMapping("/sede/{sedeId}")
    public ResponseEntity<List<Almacen>> listarPorSede(@PathVariable Long sedeId) {
        return ResponseEntity.ok(almacenService.findBySede(sedeId));
    }
    
    @GetMapping
    public ResponseEntity<List<Almacen>> listarPorTenant(@RequestParam Long tenantId) {
        return ResponseEntity.ok(almacenService.findByTenant(tenantId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Almacen> obtener(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseEntity.ok(almacenService.findById(id, tenantId));
    }
    
    @GetMapping("/sede/{sedeId}/principal")
    public ResponseEntity<Almacen> obtenerPrincipal(@PathVariable Long sedeId) {
        return ResponseEntity.ok(almacenService.findAlmacenPrincipal(sedeId));
    }
    
    @GetMapping("/frios")
    public ResponseEntity<List<Almacen>> listarFrios(@RequestParam Long tenantId) {
        return ResponseEntity.ok(almacenService.findAlmacenesFrios(tenantId));
    }
    
    @PostMapping
    public ResponseEntity<Almacen> crear(@RequestBody AlmacenDTO dto, @RequestParam Long tenantId) {
        Almacen almacen = almacenService.crear(dto, tenantId);
        return ResponseEntity.ok(almacen);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Almacen> actualizar(@PathVariable Long id, @RequestBody AlmacenDTO dto,
                                               @RequestParam Long tenantId) {
        Almacen almacen = almacenService.actualizar(id, dto, tenantId);
        return ResponseEntity.ok(almacen);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id, @RequestParam Long tenantId) {
        almacenService.desactivar(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}
