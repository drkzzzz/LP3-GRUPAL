package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.UsuariosPlataforma;
import DrinkGo.DrinkGo_backend.security.JwtUtil;
import DrinkGo.DrinkGo_backend.service.IUsuariosPlataformaService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import java.security.Principal;

@RestController
@RequestMapping("/restful")
public class UsuariosPlataformaController {
    @Autowired
    private IUsuariosPlataformaService service;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/usuarios-plataforma")
    public List<UsuariosPlataforma> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/usuarios-plataforma")
    public UsuariosPlataforma guardar(@RequestBody UsuariosPlataforma entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/usuarios-plataforma")
    public UsuariosPlataforma modificar(@RequestBody UsuariosPlataforma entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/usuarios-plataforma/{id}")
    public Optional<UsuariosPlataforma> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/usuarios-plataforma/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }

    @PostMapping("/superadmin/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String contrasena = credenciales.get("password");

        if (email == null || contrasena == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email y contraseña son requeridos"));
        }

        Optional<UsuariosPlataforma> usuarioOpt = service.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }

        UsuariosPlataforma usuario = usuarioOpt.get();

        // Verificar si el usuario está bloqueado
        if (usuario.getBloqueadoHasta() != null && usuario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Usuario bloqueado temporalmente"));
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(contrasena, usuario.getHashContrasena())) {
            // Incrementar intentos fallidos (null-safe)
            int intentos = usuario.getIntentosFallidosAcceso() != null ? usuario.getIntentosFallidosAcceso() : 0;
            usuario.setIntentosFallidosAcceso(intentos + 1);

            // Bloquear si supera 5 intentos
            if (usuario.getIntentosFallidosAcceso() >= 5) {
                usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(15));
            }

            service.modificar(usuario);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }

        // Verificar si está activo
        if (!usuario.getEstaActivo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Usuario inactivo"));
        }

        // Login exitoso - actualizar último acceso y reiniciar intentos fallidos
        usuario.setUltimoAccesoEn(LocalDateTime.now());
        usuario.setIntentosFallidosAcceso(0);
        usuario.setBloqueadoHasta(null);
        service.modificar(usuario);

        // Generar token JWT
        String token = jwtUtil.generarToken(email);

        // Preparar respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", Map.of(
                "id", usuario.getId(),
                "uuid", usuario.getUuid(),
                "email", usuario.getEmail(),
                "nombres", usuario.getNombres(),
                "apellidos", usuario.getApellidos(),
                "rol", usuario.getRol().toString()));

        return ResponseEntity.ok(response);
    }

    // ── Profile Endpoints ──────────────────────────────────────

    @GetMapping("/superadmin/perfil")
    public ResponseEntity<?> obtenerPerfil(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }

        Optional<UsuariosPlataforma> usuarioOpt = service.buscarPorEmail(principal.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }

        UsuariosPlataforma u = usuarioOpt.get();
        Map<String, Object> perfil = new HashMap<>();
        perfil.put("id", u.getId());
        perfil.put("uuid", u.getUuid());
        perfil.put("email", u.getEmail());
        perfil.put("nombres", u.getNombres());
        perfil.put("apellidos", u.getApellidos());
        perfil.put("telefono", u.getTelefono());
        perfil.put("rol", u.getRol().toString());
        perfil.put("creadoEn", u.getCreadoEn());
        perfil.put("actualizadoEn", u.getActualizadoEn());
        perfil.put("ultimoAccesoEn", u.getUltimoAccesoEn());

        return ResponseEntity.ok(perfil);
    }

    @PatchMapping("/superadmin/perfil")
    public ResponseEntity<?> actualizarPerfil(Principal principal, @RequestBody Map<String, String> datos) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }

        Optional<UsuariosPlataforma> usuarioOpt = service.buscarPorEmail(principal.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }

        UsuariosPlataforma usuario = usuarioOpt.get();

        if (datos.containsKey("nombres")) {
            String nombres = datos.get("nombres");
            if (nombres == null || nombres.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El nombre es requerido"));
            }
            usuario.setNombres(nombres.trim());
        }
        if (datos.containsKey("apellidos")) {
            String apellidos = datos.get("apellidos");
            if (apellidos == null || apellidos.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Los apellidos son requeridos"));
            }
            usuario.setApellidos(apellidos.trim());
        }
        if (datos.containsKey("telefono")) {
            String telefono = datos.get("telefono");
            if (telefono != null && !telefono.trim().isEmpty() && !telefono.trim().matches("^9\\d{8}$")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Teléfono inválido (formato: 9XXXXXXXX)"));
            }
            usuario.setTelefono(telefono != null ? telefono.trim() : null);
        }
        if (datos.containsKey("email")) {
            String email = datos.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El email es requerido"));
            }
            // Check if email is already used by another user
            Optional<UsuariosPlataforma> existing = service.buscarPorEmail(email.trim());
            if (existing.isPresent() && !existing.get().getId().equals(usuario.getId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "El email ya está en uso por otro usuario"));
            }
            usuario.setEmail(email.trim());
        }

        service.modificar(usuario);

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("id", usuario.getId());
        perfil.put("uuid", usuario.getUuid());
        perfil.put("email", usuario.getEmail());
        perfil.put("nombres", usuario.getNombres());
        perfil.put("apellidos", usuario.getApellidos());
        perfil.put("telefono", usuario.getTelefono());
        perfil.put("rol", usuario.getRol().toString());
        perfil.put("creadoEn", usuario.getCreadoEn());
        perfil.put("actualizadoEn", usuario.getActualizadoEn());
        perfil.put("ultimoAccesoEn", usuario.getUltimoAccesoEn());
        perfil.put("message", "Perfil actualizado exitosamente");

        return ResponseEntity.ok(perfil);
    }

    @PostMapping("/superadmin/perfil/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(Principal principal, @RequestBody Map<String, String> datos) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }

        String contrasenaActual = datos.get("contrasenaActual");
        String nuevaContrasena = datos.get("nuevaContrasena");
        String confirmarContrasena = datos.get("confirmarContrasena");

        // Validations
        if (contrasenaActual == null || contrasenaActual.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La contraseña actual es requerida"));
        }
        if (nuevaContrasena == null || nuevaContrasena.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La nueva contraseña es requerida"));
        }
        if (confirmarContrasena == null || confirmarContrasena.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Debe confirmar la nueva contraseña"));
        }
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Las contraseñas nuevas no coinciden"));
        }

        // Strong password validation
        if (nuevaContrasena.length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("error", "La contraseña debe tener al menos 8 caracteres"));
        }
        if (!nuevaContrasena.matches(".*[A-Z].*")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña debe contener al menos una letra mayúscula"));
        }
        if (!nuevaContrasena.matches(".*[a-z].*")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña debe contener al menos una letra minúscula"));
        }
        if (!nuevaContrasena.matches(".*\\d.*")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña debe contener al menos un número"));
        }
        if (!nuevaContrasena.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña debe contener al menos un carácter especial (!@#$%^&*...)"));
        }

        Optional<UsuariosPlataforma> usuarioOpt = service.buscarPorEmail(principal.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }

        UsuariosPlataforma usuario = usuarioOpt.get();

        // Verify current password
        if (!passwordEncoder.matches(contrasenaActual, usuario.getHashContrasena())) {
            return ResponseEntity.badRequest().body(Map.of("error", "La contraseña actual es incorrecta"));
        }

        // Verify new password is different from current
        if (passwordEncoder.matches(nuevaContrasena, usuario.getHashContrasena())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La nueva contraseña debe ser diferente a la actual"));
        }

        // Update password
        usuario.setHashContrasena(passwordEncoder.encode(nuevaContrasena));
        usuario.setContrasenaCambiadaEn(LocalDateTime.now());
        service.modificar(usuario);

        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente"));
    }
}
