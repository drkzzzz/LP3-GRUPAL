package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.RegistroRequest;
import DrinkGo.DrinkGo_backend.dto.TokenRequest;
import DrinkGo.DrinkGo_backend.dto.TokenResponse;
import DrinkGo.DrinkGo_backend.dto.UsuarioUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.Usuario;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de autenticación basado en CLASE_API_REFERENCIA.md.
 * Endpoints públicos para registro y generación de tokens JWT.
 * Usa la tabla 'usuarios' en lugar de 'registros'.
 */
@RestController
@RequestMapping("/restful")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * POST /restful/registros
     * Registra un nuevo usuario (público).
     * El UUID generado equivale al cliente_id de la clase.
     */
    @PostMapping("/registros")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest request) {
        try {
            Usuario usuario = usuarioService.registrar(request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Registro exitoso");
            response.put("clienteId", usuario.getUuid());
            response.put("email", usuario.getEmail());
            response.put("negocioId", usuario.getNegocioId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * POST /restful/token
     * Genera un token JWT (público).
     * Recibe el clienteId (UUID) y la llaveSecreta (contraseña).
     */
    @PostMapping("/token")
    public ResponseEntity<?> generarToken(@RequestBody TokenRequest request) {
        try {
            TokenResponse response = usuarioService.generarToken(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    // ============================================================
    // CRUD USUARIOS (requieren JWT)
    // ============================================================

    /**
     * GET /restful/registros
     * Listar todos los usuarios del negocio autenticado.
     * Requiere JWT.
     */
    @GetMapping("/registros")
    public ResponseEntity<?> listarUsuarios() {
        try {
            Long negocioId = obtenerNegocioId();
            List<Usuario> usuarios = usuarioService.listarUsuarios(negocioId);
            return ResponseEntity.ok(usuarios);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/registros/{id}
     * Obtener un usuario por ID.
     * Requiere JWT.
     */
    @GetMapping("/registros/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            Usuario usuario = usuarioService.obtenerUsuario(negocioId, id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /restful/registros/{id}
     * Actualizar un usuario existente.
     * Requiere JWT.
     */
    @PutMapping("/registros/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id,
                                                @RequestBody UsuarioUpdateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Usuario usuario = usuarioService.actualizarUsuario(negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario actualizado exitosamente");
            response.put("usuario", usuario);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /restful/registros/{id}
     * Eliminar usuario (borrado lógico).
     * Requiere JWT.
     */
    @DeleteMapping("/registros/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            usuarioService.eliminarUsuario(negocioId, id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario eliminado exitosamente (borrado lógico)");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No se pudo obtener la autenticación del contexto de seguridad");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerNegocioId(uuid);
    }

    private ResponseEntity<?> errorResponse(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
