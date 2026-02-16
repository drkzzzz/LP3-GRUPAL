package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.UsuarioDTO;
import DrinkGo.DrinkGo_backend.entity.Usuario;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller REST para gestión de Usuarios (V4)
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    // Endpoint de prueba: GET /api/usuarios/test
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        return ResponseEntity.ok(Map.of("status", "OK", "message", "API funcionando"));
    }
    
    @GetMapping
    public ResponseEntity<Page<Usuario>> listar(@RequestParam Long tenantId, Pageable pageable) {
        return ResponseEntity.ok(usuarioService.findByTenant(tenantId, pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseEntity.ok(usuarioService.findById(id, tenantId));
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Usuario>> buscar(@RequestParam Long tenantId, @RequestParam String q) {
        return ResponseEntity.ok(usuarioService.buscar(tenantId, q));
    }
    
    @GetMapping("/por-rol/{rolCodigo}")
    public ResponseEntity<List<Usuario>> listarPorRol(@PathVariable String rolCodigo,
                                                       @RequestParam Long tenantId) {
        return ResponseEntity.ok(usuarioService.findByRol(tenantId, rolCodigo));
    }
    
    @GetMapping("/por-sede/{sedeId}")
    public ResponseEntity<List<Usuario>> listarPorSede(@PathVariable Long sedeId) {
        return ResponseEntity.ok(usuarioService.findBySede(sedeId));
    }
    
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody UsuarioDTO dto, @RequestParam Long tenantId) {
        Usuario usuario = usuarioService.crear(dto, tenantId);
        return ResponseEntity.ok(usuario);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto,
                                               @RequestParam Long tenantId) {
        Usuario usuario = usuarioService.actualizar(id, dto, tenantId);
        return ResponseEntity.ok(usuario);
    }
    
    @PostMapping("/{id}/cambiar-contrasena")
    public ResponseEntity<Map<String, String>> cambiarContrasena(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @RequestParam Long tenantId) {
        String actual = request.get("contrasenaActual");
        String nueva = request.get("contrasenaNueva");
        usuarioService.cambiarContrasena(id, actual, nueva, tenantId);
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
    }
    
    @PostMapping("/{id}/asignar-roles")
    public ResponseEntity<Usuario> asignarRoles(@PathVariable Long id,
                                                 @RequestBody Map<String, List<Long>> request,
                                                 @RequestParam Long tenantId) {
        Set<Long> rolesIds = new HashSet<>(request.get("rolesIds"));
        Usuario usuario = usuarioService.asignarRoles(id, rolesIds, tenantId);
        return ResponseEntity.ok(usuario);
    }
    
    @PostMapping("/{id}/asignar-sedes")
    public ResponseEntity<Usuario> asignarSedes(@PathVariable Long id,
                                                 @RequestBody Map<String, List<Long>> request,
                                                 @RequestParam Long tenantId) {
        Set<Long> sedesIds = new HashSet<>(request.get("sedesIds"));
        Usuario usuario = usuarioService.asignarSedes(id, sedesIds, tenantId);
        return ResponseEntity.ok(usuario);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id, @RequestParam Long tenantId) {
        usuarioService.desactivar(id, tenantId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/login-pin")
    public ResponseEntity<Usuario> loginPorPin(@RequestBody Map<String, Object> request) {
        Long tenantId = Long.valueOf(request.get("tenantId").toString());
        String pin = (String) request.get("pin");
        Usuario usuario = usuarioService.loginPorPin(tenantId, pin);
        return ResponseEntity.ok(usuario);
    }
}
