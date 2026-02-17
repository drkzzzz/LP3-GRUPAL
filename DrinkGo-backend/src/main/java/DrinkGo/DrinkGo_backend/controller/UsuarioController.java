package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.UsuarioDTO;
import DrinkGo.DrinkGo_backend.dto.UsuarioUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.Usuario;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de Usuarios
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
    public ResponseEntity<List<Usuario>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(usuarioService.listarUsuarios(negocioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(usuarioService.obtenerUsuario(negocioId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody UsuarioUpdateRequest request) {
        Long negocioId = obtenerNegocioId();
        Usuario usuario = usuarioService.actualizarUsuario(negocioId, id, request);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Long negocioId = obtenerNegocioId();
        usuarioService.eliminarUsuario(negocioId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extrae negocioId del contexto de seguridad JWT
     */
    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerNegocioId(uuid);
    }
}
