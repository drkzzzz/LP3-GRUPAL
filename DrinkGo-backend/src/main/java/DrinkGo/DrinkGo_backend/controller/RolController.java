package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.RolDTO;
import DrinkGo.DrinkGo_backend.entity.Permiso;
import DrinkGo.DrinkGo_backend.entity.Rol;
import DrinkGo.DrinkGo_backend.service.RolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller REST para gestión de Roles y Permisos (V4)
 */
@RestController
@RequestMapping("/api/roles")
public class RolController {
    
    private final RolService rolService;
    
    public RolController(RolService rolService) {
        this.rolService = rolService;
    }
    
    @GetMapping
    public ResponseEntity<List<Rol>> listarDisponibles(@RequestParam Long tenantId) {
        return ResponseEntity.ok(rolService.findRolesDisponibles(tenantId));
    }
    
    @GetMapping("/globales")
    public ResponseEntity<List<Rol>> listarGlobales() {
        return ResponseEntity.ok(rolService.findRolesGlobales());
    }
    
    @GetMapping("/personalizados")
    public ResponseEntity<List<Rol>> listarPersonalizados(@RequestParam Long tenantId) {
        return ResponseEntity.ok(rolService.findRolesPersonalizados(tenantId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Rol> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.findById(id));
    }
    
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Rol> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(rolService.findByCodigo(codigo));
    }
    
    @PostMapping
    public ResponseEntity<Rol> crear(@RequestBody RolDTO dto, @RequestParam Long tenantId) {
        Rol rol = rolService.crear(dto, tenantId);
        return ResponseEntity.ok(rol);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Rol> actualizar(@PathVariable Long id, @RequestBody RolDTO dto,
                                           @RequestParam Long tenantId) {
        Rol rol = rolService.actualizar(id, dto, tenantId);
        return ResponseEntity.ok(rol);
    }
    
    @PostMapping("/{id}/asignar-permisos")
    public ResponseEntity<Rol> asignarPermisos(@PathVariable Long id,
                                                @RequestBody Map<String, List<Long>> request,
                                                @RequestParam Long tenantId) {
        Set<Long> permisosIds = new HashSet<>(request.get("permisosIds"));
        Rol rol = rolService.asignarPermisos(id, permisosIds, tenantId);
        return ResponseEntity.ok(rol);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id, @RequestParam Long tenantId) {
        rolService.desactivar(id, tenantId);
        return ResponseEntity.noContent().build();
    }
    
    // Permisos
    @GetMapping("/permisos")
    public ResponseEntity<List<Permiso>> listarPermisos() {
        return ResponseEntity.ok(rolService.findPermisos());
    }
    
    @GetMapping("/{rolId}/permisos")
    public ResponseEntity<List<Permiso>> listarPermisosPorRol(@PathVariable Long rolId) {
        return ResponseEntity.ok(rolService.findPermisosByRol(rolId));
    }
}
