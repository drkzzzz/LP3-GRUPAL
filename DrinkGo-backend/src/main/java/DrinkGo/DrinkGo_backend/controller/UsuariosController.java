package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Negocios;
import DrinkGo.DrinkGo_backend.entity.Usuarios;
import DrinkGo.DrinkGo_backend.security.JwtUtil;
import DrinkGo.DrinkGo_backend.service.IUsuariosService;
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
public class UsuariosController {
    @Autowired
    private IUsuariosService service;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/usuarios")
    public List<Usuarios> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/usuarios")
    public Usuarios guardar(@RequestBody Usuarios entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/usuarios")
    public Usuarios modificar(@RequestBody Usuarios entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/usuarios/{id}")
    public Optional<Usuarios> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @GetMapping("/usuarios/por-negocio/{negocioId}")
    public List<Usuarios> buscarPorNegocio(@PathVariable("negocioId") Long negocioId) {
        return service.buscarPorNegocio(negocioId);
    }

    @DeleteMapping("/usuarios/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }

    @PostMapping("/admin/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String contrasena = credenciales.get("password");

        if (email == null || contrasena == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email y contraseña son requeridos"));
        }

        Optional<Usuarios> usuarioOpt = service.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }

        Usuarios usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(contrasena, usuario.getHashContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }

        if (!Boolean.TRUE.equals(usuario.getEstaActivo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Usuario inactivo"));
        }

        // Actualizar último acceso
        usuario.setUltimoAccesoEn(LocalDateTime.now());
        service.modificar(usuario);

        String token = jwtUtil.generarToken(email);

        Negocios negocio = usuario.getNegocio();
        Map<String, Object> negocioData = new HashMap<>();
        negocioData.put("id", negocio.getId());
        negocioData.put("razonSocial", negocio.getRazonSocial());
        negocioData.put("nombre", negocio.getNombreComercial());
        negocioData.put("ruc", negocio.getRuc());
        negocioData.put("estado", negocio.getEstado());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", Map.of(
                "id", usuario.getId(),
                "uuid", usuario.getUuid() != null ? usuario.getUuid() : "",
                "email", usuario.getEmail(),
                "nombres", usuario.getNombres(),
                "apellidos", usuario.getApellidos()));
        response.put("negocio", negocioData);

        return ResponseEntity.ok(response);
    }
}
