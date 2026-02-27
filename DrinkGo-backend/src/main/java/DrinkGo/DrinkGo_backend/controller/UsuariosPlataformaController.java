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
}
