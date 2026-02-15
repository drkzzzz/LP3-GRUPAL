package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.LoginRequest;
import DrinkGo.DrinkGo_backend.dto.LoginResponse;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.repository.*;
import DrinkGo.DrinkGo_backend.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de autenticación.
 * Implementa el flujo completo de login con JWT y sesiones_usuario.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final NegocioRepository negocioRepository;
    private final SesionUsuarioRepository sesionUsuarioRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
                       UsuarioRolRepository usuarioRolRepository,
                       NegocioRepository negocioRepository,
                       SesionUsuarioRepository sesionUsuarioRepository,
                       JwtUtil jwtUtil,
                       BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.negocioRepository = negocioRepository;
        this.sesionUsuarioRepository = sesionUsuarioRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Proceso de login obligatorio:
     * 1. Buscar usuario por email
     * 2. Validar contraseña con BCrypt
     * 3. Verificar: esta_activo=1, bloqueado_hasta IS NULL, eliminado_en IS NULL
     * 4. Obtener negocio_id y verificar negocio activo
     * 5. Obtener rol del usuario
     * 6. Generar JWT firmado
     * 7. Generar hash SHA-256 del token
     * 8. Insertar en sesiones_usuario
     * 9. Retornar token al cliente
     */
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {

        // 1. Buscar usuario por email (y negocio_id si se proporcionó)
        Usuario usuario;
        if (request.getNegocioId() != null) {
            // Login dirigido a un negocio específico
            usuario = usuarioRepository
                    .findByEmailAndNegocioIdAndEstaActivoTrueAndEliminadoEnIsNull(
                            request.getEmail(), request.getNegocioId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Credenciales inválidas: email no encontrado en el negocio especificado o usuario inactivo"));
        } else {
            // Sin negocio_id: buscar todos los usuarios con ese email
            List<Usuario> usuarios = usuarioRepository
                    .findByEmailAndEstaActivoTrueAndEliminadoEnIsNull(request.getEmail());

            if (usuarios.isEmpty()) {
                throw new IllegalArgumentException("Credenciales inválidas: email no encontrado o usuario inactivo");
            }

            if (usuarios.size() > 1) {
                // Ambigüedad: mismo email en múltiples negocios
                throw new IllegalArgumentException(
                        "El email está asociado a múltiples negocios. " +
                        "Incluye el campo 'negocioId' en la solicitud para especificar a cuál negocio deseas acceder.");
            }

            usuario = usuarios.get(0);
        }

        // 2. Validar contraseña con BCrypt
        if (!passwordEncoder.matches(request.getPassword(), usuario.getHashContrasena())) {
            throw new IllegalArgumentException("Credenciales inválidas: contraseña incorrecta");
        }

        // 3. Verificar bloqueado_hasta
        if (usuario.getBloqueadoHasta() != null && usuario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Usuario bloqueado temporalmente hasta: " + usuario.getBloqueadoHasta());
        }

        // 4. Obtener y validar negocio
        Long negocioId = usuario.getNegocioId();
        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new IllegalArgumentException("Negocio asociado no encontrado"));

        if (!"activo".equals(negocio.getEstado())) {
            throw new IllegalArgumentException("El negocio no está activo (estado: " + negocio.getEstado() + ")");
        }
        if (!Boolean.TRUE.equals(negocio.getEstaActivo())) {
            throw new IllegalArgumentException("El negocio está desactivado");
        }
        if (negocio.getEliminadoEn() != null) {
            throw new IllegalArgumentException("El negocio ha sido eliminado");
        }

        // 5. Obtener rol del usuario
        List<UsuarioRol> roles = usuarioRolRepository.findByUsuarioId(usuario.getId());
        String rolNombre = "sin_rol";
        if (!roles.isEmpty()) {
            Rol rol = roles.get(0).getRol();
            if (rol != null) {
                rolNombre = rol.getSlug() != null ? rol.getSlug() : rol.getNombre();
            }
        }

        // 6. Generar JWT con claims: sub=usuario_id, negocio_id, rol, iat, exp
        String token = jwtUtil.generarToken(usuario.getId(), negocioId, rolNombre);

        // 7. Generar hash SHA-256 del token
        String hashToken = JwtUtil.generarHashSHA256(token);

        // 8. Insertar en sesiones_usuario
        SesionUsuario sesion = new SesionUsuario();
        sesion.setUsuarioId(usuario.getId());
        sesion.setHashToken(hashToken);
        sesion.setDireccionIp(httpRequest.getRemoteAddr());
        sesion.setAgenteUsuario(httpRequest.getHeader("User-Agent"));
        sesion.setExpiraEn(LocalDateTime.now().plus(jwtUtil.getExpirationMs(), ChronoUnit.MILLIS));
        sesion.setUltimaActividadEn(LocalDateTime.now());
        sesion.setEstaActivo(true);
        sesion.setCreadoEn(LocalDateTime.now());
        sesionUsuarioRepository.save(sesion);

        // Actualizar último acceso del usuario
        usuario.setUltimoAccesoEn(LocalDateTime.now());
        usuario.setIpUltimoAcceso(httpRequest.getRemoteAddr());
        usuarioRepository.save(usuario);

        // 9. Retornar token al cliente
        String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();
        return new LoginResponse(token, usuario.getId(), negocioId, nombreCompleto, rolNombre);
    }
}
