package DrinkGo.DrinkGo_backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import DrinkGo.DrinkGo_backend.entity.CajasRegistradoras;
import DrinkGo.DrinkGo_backend.entity.Negocios;
import DrinkGo.DrinkGo_backend.entity.Sedes;
import DrinkGo.DrinkGo_backend.entity.Suscripciones;
import DrinkGo.DrinkGo_backend.entity.Usuarios;
import DrinkGo.DrinkGo_backend.entity.UsuariosRoles;
import DrinkGo.DrinkGo_backend.entity.UsuariosSedes;
import DrinkGo.DrinkGo_backend.repository.CajasRegistradorasRepository;
import DrinkGo.DrinkGo_backend.repository.SuscripcionesRepository;
import DrinkGo.DrinkGo_backend.repository.UsuariosRepository;
import DrinkGo.DrinkGo_backend.repository.UsuariosRolesRepository;
import DrinkGo.DrinkGo_backend.repository.UsuariosSedesRepository;
import DrinkGo.DrinkGo_backend.security.JwtUtil;
import DrinkGo.DrinkGo_backend.service.IUsuariosService;

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

    @Autowired
    private UsuariosSedesRepository usuariosSedesRepo;

    @Autowired
    private CajasRegistradorasRepository cajasRepo;

    @Autowired
    private UsuariosRepository usuariosRepo;

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

    // ── Profile Endpoints ──────────────────────────────────────

    @GetMapping("/admin/perfil")
    public ResponseEntity<?> obtenerPerfil(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }
        Optional<Usuarios> usuarioOpt = service.buscarPorEmail(principal.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
        Usuarios u = usuarioOpt.get();

        List<UsuariosRoles> roles = usuariosRolesRepo.findByUsuarioId(u.getId());
        List<String> roleNames = roles.stream()
                .map(ur -> ur.getRolNombre() != null ? ur.getRolNombre() : "")
                .filter(n -> !n.isBlank())
                .toList();

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("id", u.getId());
        perfil.put("uuid", u.getUuid() != null ? u.getUuid() : "");
        perfil.put("email", u.getEmail());
        perfil.put("nombres", u.getNombres());
        perfil.put("apellidos", u.getApellidos());
        perfil.put("telefono", u.getTelefono());
        perfil.put("tipoDocumento", u.getTipoDocumento() != null ? u.getTipoDocumento().toString() : null);
        perfil.put("numeroDocumento", u.getNumeroDocumento());
        perfil.put("roles", roleNames);
        perfil.put("creadoEn", u.getCreadoEn());
        perfil.put("actualizadoEn", u.getActualizadoEn());
        perfil.put("ultimoAccesoEn", u.getUltimoAccesoEn());
        if (u.getNegocio() != null) {
            Map<String, Object> negocioMap = new HashMap<>();
            negocioMap.put("id", u.getNegocio().getId());
            negocioMap.put("nombre", u.getNegocio().getNombreComercial());
            negocioMap.put("razonSocial", u.getNegocio().getRazonSocial());
            negocioMap.put("ruc", u.getNegocio().getRuc());
            perfil.put("negocio", negocioMap);
        }
        return ResponseEntity.ok(perfil);
    }

    @PatchMapping("/admin/perfil")
    public ResponseEntity<?> actualizarPerfil(Principal principal, @RequestBody Map<String, String> datos) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }
        Optional<Usuarios> usuarioOpt = service.buscarPorEmail(principal.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
        Usuarios usuario = usuarioOpt.get();

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
            Optional<Usuarios> existing = service.buscarPorEmail(email.trim());
            if (existing.isPresent() && !existing.get().getId().equals(usuario.getId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "El email ya está en uso por otro usuario"));
            }
            usuario.setEmail(email.trim());
        }

        service.modificar(usuario);

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("id", usuario.getId());
        perfil.put("email", usuario.getEmail());
        perfil.put("nombres", usuario.getNombres());
        perfil.put("apellidos", usuario.getApellidos());
        perfil.put("telefono", usuario.getTelefono());
        perfil.put("actualizadoEn", usuario.getActualizadoEn());
        perfil.put("message", "Perfil actualizado exitosamente");
        return ResponseEntity.ok(perfil);
    }

    @PostMapping("/admin/perfil/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(Principal principal, @RequestBody Map<String, String> datos) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }
        String contrasenaActual = datos.get("contrasenaActual");
        String nuevaContrasena = datos.get("nuevaContrasena");
        String confirmarContrasena = datos.get("confirmarContrasena");

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

        Optional<Usuarios> usuarioOpt = service.buscarPorEmail(principal.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
        Usuarios usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(contrasenaActual, usuario.getHashContrasena())) {
            return ResponseEntity.badRequest().body(Map.of("error", "La contraseña actual es incorrecta"));
        }
        if (passwordEncoder.matches(nuevaContrasena, usuario.getHashContrasena())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La nueva contraseña debe ser diferente a la actual"));
        }

        usuario.setHashContrasena(passwordEncoder.encode(nuevaContrasena));
        service.modificar(usuario);
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente"));
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

        // Verificar si el usuario está bloqueado
        if (usuario.getBloqueadoHasta() != null && usuario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Usuario bloqueado temporalmente. Intente de nuevo más tarde."));
        }

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

        // Login exitoso - actualizar último acceso y reiniciar intentos fallidos
        usuario.setUltimoAccesoEn(LocalDateTime.now());
        usuario.setIntentosFallidosAcceso(0);
        usuario.setBloqueadoHasta(null);
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
        // Obtener la sede asignada al usuario (predeterminada o primera disponible)
        List<UsuariosSedes> usuarioSedes = usuariosSedesRepo.findByUsuarioId(usuario.getId());

        // Bloquear login si el usuario no tiene sede asignada
        if (usuarioSedes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error",
                            "Tu cuenta no tiene una sede asignada. Contacta al administrador del negocio."));
        }

        Map<String, Object> sedeData = null;
        if (!usuarioSedes.isEmpty()) {
            UsuariosSedes us = usuarioSedes.stream()
                    .filter(x -> Boolean.TRUE.equals(x.getEsPredeterminado()))
                    .findFirst()
                    .orElse(usuarioSedes.get(0));
            Sedes s = us.getSede();
            sedeData = new HashMap<>();
            sedeData.put("id", s.getId());
            sedeData.put("codigo", s.getCodigo());
            sedeData.put("nombre", s.getNombre());
            sedeData.put("direccion", s.getDireccion());
            sedeData.put("ciudad", s.getCiudad() != null ? s.getCiudad() : "");
            sedeData.put("departamento", s.getDepartamento() != null ? s.getDepartamento() : "");
            sedeData.put("telefono", s.getTelefono() != null ? s.getTelefono() : "");
            sedeData.put("esPrincipal", Boolean.TRUE.equals(s.getEsPrincipal()));
            sedeData.put("deliveryHabilitado", Boolean.TRUE.equals(s.getDeliveryHabilitado()));
            sedeData.put("recojoHabilitado", Boolean.TRUE.equals(s.getRecojoHabilitado()));
        }

        response.put("token", token);
        response.put("usuario", Map.of(
                "id", usuario.getId(),
                "uuid", usuario.getUuid() != null ? usuario.getUuid() : "",
                "email", usuario.getEmail(),
                "nombres", usuario.getNombres(),
                "apellidos", usuario.getApellidos()));
        response.put("negocio", negocioData);
        if (sedeData != null) {
            response.put("sede", sedeData);
        }

        // ── Permisos: módulos habilitados desde suscripción JSON ∩ permisos del rol ──
        // Prioridad: suscripcion.modulosPersonalizados > plan.modulosHabilitados > sin
        // restricción
        // 1. Determinar módulos habilitados del negocio (null = sin restricción)
        Set<String> codigosHabilitados = null;
        if (suscripcionOpt.isPresent()) {
            Suscripciones sus = suscripcionOpt.get();
            String modulosJson = sus.getModulosPersonalizados();
            if (modulosJson == null && sus.getPlan() != null) {
                modulosJson = sus.getPlan().getModulosHabilitados();
            }
            if (modulosJson != null && !modulosJson.isBlank()) {
                try {
                    // Parse JSON array ["dashboard","ventas",...] →
                    // Set<"m.dashboard","m.ventas",...>
                    String clean = modulosJson.replaceAll("[\\[\\]\"\\s]", "");
                    codigosHabilitados = new HashSet<>();
                    for (String part : clean.split(",")) {
                        if (!part.isBlank()) {
                            codigosHabilitados.add("m." + part.trim());
                        }
                    }
                } catch (Exception ignored) {
                    // Si falla el parse → sin restricción
                }
            }
        }

        // 2. Permisos del rol del usuario CON alcance
        List<Object[]> rolePermisosConAlcance = usuariosRolesRepo
                .findPermisosConAlcanceByUsuarioId(usuario.getId());

        // 3. Calcular permisos finales con alcance
        List<Map<String, String>> permisosFinales = new ArrayList<>();

        if (rolePermisosConAlcance.isEmpty()) {
            // Sin rol asignado → todos los módulos base con alcance completo
            List<String> modulosBase;
            if (codigosHabilitados == null) {
                modulosBase = List.of("m.dashboard", "m.configuracion", "m.usuarios",
                        "m.catalogo", "m.inventario", "m.compras",
                        "m.ventas", "m.pedidos", "m.devoluciones",
                        "m.facturacion", "m.reportes");
            } else {
                modulosBase = new ArrayList<>(codigosHabilitados);
            }
            for (String codigo : modulosBase) {
                permisosFinales.add(Map.of("codigo", codigo, "alcance", "completo"));
            }
        } else {
            for (Object[] row : rolePermisosConAlcance) {
                String codigo = (String) row[0];
                String alcance = row[1] != null ? row[1].toString() : "completo";
                // Filtrar por plan si aplica
                if (codigosHabilitados != null && !codigosHabilitados.contains(codigo)) {
                    continue;
                }
                permisosFinales.add(Map.of("codigo", codigo, "alcance", alcance));
            }
        }
        response.put("permisos", permisosFinales);

        // 4. Caja asignada al usuario (si existe)
        List<CajasRegistradoras> cajasAsignadas = cajasRepo.findByUsuarioAsignadoId(usuario.getId());
        if (!cajasAsignadas.isEmpty()) {
            CajasRegistradoras caja = cajasAsignadas.get(0); // primera caja asignada
            Map<String, Object> cajaData = new HashMap<>();
            cajaData.put("id", caja.getId());
            cajaData.put("nombreCaja", caja.getNombreCaja());
            cajaData.put("codigo", caja.getCodigo());
            response.put("cajaAsignada", cajaData);
        }

        if (suscripcionData != null) {
            response.put("suscripcion", suscripcionData);
        }

        return ResponseEntity.ok(response);
    }

    // ── Desbloquear usuario admin de negocio (solo superadmin) ────────────────

    @PatchMapping("/superadmin/usuarios/{id}/desbloquear")
    public ResponseEntity<?> desbloquearUsuario(@PathVariable Long id) {
        Optional<Usuarios> opt = usuariosRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado"));
        }
        Usuarios usuario = opt.get();
        usuario.setIntentosFallidosAcceso(0);
        usuario.setBloqueadoHasta(null);
        service.modificar(usuario);
        return ResponseEntity.ok(Map.of("message", "Usuario desbloqueado exitosamente"));
    }
}
