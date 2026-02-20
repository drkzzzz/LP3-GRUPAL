package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.ZonaDeliveryDTO;
import DrinkGo.DrinkGo_backend.service.ZonaDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants/{tenantId}/zonas-delivery")
@CrossOrigin(origins = "*")
public class ZonaDeliveryController {
    
    @Autowired
    private ZonaDeliveryService zonaDeliveryService;
    
    // Listar todas las zonas del negocio
    @GetMapping
    public ResponseEntity<List<ZonaDeliveryDTO>> listarTodasZonas(@PathVariable Long tenantId) {
        List<ZonaDeliveryDTO> zonas = zonaDeliveryService.listarZonasPorNegocio(tenantId);
        return ResponseEntity.ok(zonas);
    }
    
    // Crear zona de delivery
    @PostMapping
    public ResponseEntity<ZonaDeliveryDTO> crearZona(
            @PathVariable Long tenantId,
            @RequestBody ZonaDeliveryDTO dto) {
        ZonaDeliveryDTO zona = zonaDeliveryService.crearZona(tenantId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(zona);
    }
    
    // Obtener zona por ID
    @GetMapping("/{id}")
    public ResponseEntity<ZonaDeliveryDTO> obtenerZona(
            @PathVariable Long tenantId,
            @PathVariable Long id) {
        ZonaDeliveryDTO zona = zonaDeliveryService.obtenerZona(tenantId, id);
        return ResponseEntity.ok(zona);
    }
    
    // Listar zonas por sede
    @GetMapping("/sedes/{sedeId}")
    public ResponseEntity<List<ZonaDeliveryDTO>> listarZonasPorSede(
            @PathVariable Long tenantId,
            @PathVariable Long sedeId) {
        List<ZonaDeliveryDTO> zonas = zonaDeliveryService.listarZonasPorSede(tenantId, sedeId);
        return ResponseEntity.ok(zonas);
    }
    
    // Listar zonas activas por sede
    @GetMapping("/sedes/{sedeId}/activas")
    public ResponseEntity<List<ZonaDeliveryDTO>> listarZonasActivasPorSede(
            @PathVariable Long tenantId,
            @PathVariable Long sedeId) {
        List<ZonaDeliveryDTO> zonas = zonaDeliveryService.listarZonasActivasPorSede(tenantId, sedeId);
        return ResponseEntity.ok(zonas);
    }
    
    // Buscar zona por distrito
    @GetMapping("/sedes/{sedeId}/buscar")
    public ResponseEntity<ZonaDeliveryDTO> buscarZonaPorDistrito(
            @PathVariable Long tenantId,
            @PathVariable Long sedeId,
            @RequestParam String distrito) {
        ZonaDeliveryDTO zona = zonaDeliveryService.buscarZonaPorDistrito(tenantId, sedeId, distrito);
        return ResponseEntity.ok(zona);
    }
    
    // Actualizar zona
    @PutMapping("/{id}")
    public ResponseEntity<ZonaDeliveryDTO> actualizarZona(
            @PathVariable Long tenantId,
            @PathVariable Long id,
            @RequestBody ZonaDeliveryDTO dto) {
        ZonaDeliveryDTO zona = zonaDeliveryService.actualizarZona(tenantId, id, dto);
        return ResponseEntity.ok(zona);
    }
    
    // Activar zona
    @PostMapping("/{id}/activar")
    public ResponseEntity<ZonaDeliveryDTO> activarZona(
            @PathVariable Long tenantId,
            @PathVariable Long id) {
        ZonaDeliveryDTO zona = zonaDeliveryService.cambiarEstadoZona(tenantId, id, true);
        return ResponseEntity.ok(zona);
    }
    
    // Desactivar zona
    @PostMapping("/{id}/desactivar")
    public ResponseEntity<ZonaDeliveryDTO> desactivarZona(
            @PathVariable Long tenantId,
            @PathVariable Long id) {
        ZonaDeliveryDTO zona = zonaDeliveryService.cambiarEstadoZona(tenantId, id, false);
        return ResponseEntity.ok(zona);
    }
    
    // Eliminar zona
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarZona(
            @PathVariable Long tenantId,
            @PathVariable Long id) {
        zonaDeliveryService.eliminarZona(tenantId, id);
        return ResponseEntity.noContent().build();
    }
}
