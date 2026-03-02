package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Negocios;
import DrinkGo.DrinkGo_backend.entity.Suscripciones;
import DrinkGo.DrinkGo_backend.entity.Usuarios;
import DrinkGo.DrinkGo_backend.entity.UsuariosRoles;
import DrinkGo.DrinkGo_backend.repository.SuscripcionesRepository;
import DrinkGo.DrinkGo_backend.repository.UsuariosRolesRepository;
import DrinkGo.DrinkGo_backend.security.JwtUtil;
import DrinkGo.DrinkGo_backend.service.IUsuariosService;
import java.time.LocalDate;
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

    @Autowired
    private SuscripcionesRepository repoSuscripciones;

    @Autowired
    private UsuariosRolesRepository usuariosRolesRepo;

    @GetMapping("/usuarios/{id}/permisos")
    public List<String> obtenerPermisos(@PathVariable Long id) {
        return usuariosRolesRepo.findCodigosPermisoByUsuarioId(id);
    }

    @GetMapping("/usuarios/{id}/roles")
    public List<UsuariosRoles> getRolesByUsuario(@PathVariable("id") Long id) {
        return usuariosRolesRepo.findByUsuarioId(id);
    }

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
    public ResponseEntity<?> modificar(@RequestBody Usuarios incomingData) {
        Optional<Usuarios> existingOpt = service.buscarId(incomingData.getId());
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
        Usuarios existing = existingOpt.get();

        // Actualizar solo los campos editables
        existing.setNombres(incomingData.getNombres());
        existing.setApellidos(incomingData.getApellidos());
        existing.setEmail(incomingData.getEmail());
        existing.setTipoDocumento(incomingData.getTipoDocumento());
        existing.setNumeroDocumento(incomingData.getNumeroDocumento());
        existing.setTelefono(incomingData.getTelefono());
        if (incomingData.getEstaActivo() != null) {
            existing.setEstaActivo(incomingData.getEstaActivo());
        }

        // Solo actualizar contraseña si se envió una nueva
        if (incomingData.getHashContrasena() != null && !incomingData.getHashContrasena().isBlank()) {
            existing.setHashContrasena(incomingData.getHashContrasena());
        }

        service.modificar(existing);
        return ResponseEntity.ok(existing);
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

        // Verificar que el negocio esté activo
        Negocios negocio = usuario.getNegocio();
        if (negocio == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Usuario no tiene un negocio asignado"));
        }
        if (negocio.getEstado() != Negocios.EstadoNegocio.activo) {
            String estadoMsg = negocio.getEstado().name();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error",
                            "No se puede iniciar sesión. El negocio se encuentra " + estadoMsg + "."));
        }

        // Verificar suscripción activa y vigente
        Optional<Suscripciones> suscripcionOpt = repoSuscripciones
                .findFirstByNegocioIdAndEstado(negocio.getId(), Suscripciones.EstadoSuscripcion.activa);
        if (suscripcionOpt.isEmpty()) {
            // Buscar cualquier suscripcion como fallback
            suscripcionOpt = repoSuscripciones.findFirstByNegocioIdOrderByIdDesc(negocio.getId());
        }

        if (suscripcionOpt.isPresent()) {
            Suscripciones suscripcion = suscripcionOpt.get();
            LocalDate hoy = LocalDate.now();
            if (suscripcion.getFinPeriodoActual() != null
                    && suscripcion.getFinPeriodoActual().isBefore(hoy)) {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                        .body(Map.of("error",
                                "Tu suscripción ha vencido el " + suscripcion.getFinPeriodoActual()
                                        + ". Por favor, contacta a soporte para regularizar tu pago."));
            }
        }

        // Actualizar último acceso
        usuario.setUltimoAccesoEn(LocalDateTime.now());
        service.modificar(usuario);

        String token = jwtUtil.generarToken(email);

        // Datos de suscripcion para el cliente
        Map<String, Object> suscripcionData = null;
        if (suscripcionOpt.isPresent()) {
            Suscripciones s = suscripcionOpt.get();
            suscripcionData = new HashMap<>();
            suscripcionData.put("id", s.getId());
            suscripcionData.put("estado", s.getEstado());
            suscripcionData.put("inicioPeriodoActual", s.getInicioPeriodoActual());
            suscripcionData.put("finPeriodoActual", s.getFinPeriodoActual());
            if (s.getPlan() != null) {
                suscripcionData.put("planNombre", s.getPlan().getNombre());
                suscripcionData.put("planPrecio", s.getPlan().getPrecio());
            }
        }

        Map<String, Object> negocioData = new HashMap<>();
        negocioData.put("id", negocio.getId());
        negocioData.put("razonSocial", negocio.getRazonSocial());
        negocioData.put("nombre", negocio.getNombreComercial());
        negocioData.put("ruc", negocio.getRuc());
        negocioData.put("estado", negocio.getEstado());
        negocioData.put("tienePse", negocio.getTienePse());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", Map.of(
                "id", usuario.getId(),
                "uuid", usuario.getUuid() != null ? usuario.getUuid() : "",
                "email", usuario.getEmail(),
                "nombres", usuario.getNombres(),
                "apellidos", usuario.getApellidos()));
        response.put("negocio", negocioData);
        response.put("permisos", usuariosRolesRepo.findCodigosPermisoByUsuarioId(usuario.getId()));
        if (suscripcionData != null) {
            response.put("suscripcion", suscripcionData);
        }

        return ResponseEntity.ok(response);
    }
}
