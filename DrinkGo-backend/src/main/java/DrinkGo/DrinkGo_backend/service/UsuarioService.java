package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.RegistroRequest;
import DrinkGo.DrinkGo_backend.dto.TokenRequest;
import DrinkGo.DrinkGo_backend.dto.TokenResponse;
import DrinkGo.DrinkGo_backend.dto.UsuarioUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.SesionUsuario;
import DrinkGo.DrinkGo_backend.entity.Usuario;
import DrinkGo.DrinkGo_backend.repository.SesionUsuarioRepository;
import DrinkGo.DrinkGo_backend.repository.UsuarioRepository;
import DrinkGo.DrinkGo_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de autenticación - Reemplaza RegistroService.
 * Usa la tabla 'usuarios' para registro y 'sesiones_usuario' para tokens JWT.
 *
 * Equivalencias con CLASE_API_REFERENCIA.md:
 *   Registro       → Usuario (tabla 'usuarios')
 *   cliente_id     → uuid (identificador único del usuario)
 *   llave_secreta  → hash_contrasena (BCrypt)
 *   access_token   → sesiones_usuario.hash_token
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SesionUsuarioRepository sesionRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Registrar un nuevo usuario.
     * Crea el usuario en 'usuarios', genera UUID (equivalente a cliente_id),
     * encripta contraseña con BCrypt, y devuelve los datos del registro.
     */
    @Transactional
    public Usuario registrar(RegistroRequest request) {
        // Validar campos obligatorios
        if (request.getNombres() == null || request.getNombres().isBlank()) {
            throw new RuntimeException("El campo 'nombres' es obligatorio");
        }
        if (request.getApellidos() == null || request.getApellidos().isBlank()) {
            throw new RuntimeException("El campo 'apellidos' es obligatorio");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("El campo 'email' es obligatorio");
        }
        if (request.getLlaveSecreta() == null || request.getLlaveSecreta().isBlank()) {
            throw new RuntimeException("El campo 'llaveSecreta' es obligatorio");
        }
        if (request.getNegocioId() == null) {
            throw new RuntimeException("El campo 'negocioId' es obligatorio");
        }

        // Verificar que el email no esté registrado
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear el usuario en la tabla 'usuarios'
        Usuario usuario = new Usuario();
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(request.getEmail());
        usuario.setNegocioId(request.getNegocioId());

        // Encriptar contraseña con BCrypt (equivalente a llave_secreta)
        usuario.setHashContrasena(request.getLlaveSecreta());

        // El UUID se genera automáticamente en @PrePersist (equivalente a cliente_id)
        return usuarioRepository.save(usuario);
    }

    /**
     * Generar token JWT para un usuario registrado.
     * Valida credenciales (UUID + contraseña), genera JWT,
     * y lo almacena en 'sesiones_usuario' (equivalente a guardar access_token).
     */
    @Transactional
    public TokenResponse generarToken(TokenRequest request) {
        if (request.getClienteId() == null || request.getClienteId().isBlank()) {
            throw new RuntimeException("El campo 'clienteId' es obligatorio");
        }
        if (request.getLlaveSecreta() == null || request.getLlaveSecreta().isBlank()) {
            throw new RuntimeException("El campo 'llaveSecreta' es obligatorio");
        }

        // Buscar usuario por UUID (equivalente a buscar por cliente_id)
        Usuario usuario = usuarioRepository.findByUuid(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que esté activo
        if (!usuario.getEstaActivo()) {
            throw new RuntimeException("El usuario está desactivado");
        }

        // Validar contraseña con BCrypt (equivalente a validar llave_secreta)
        if (!passwordEncoder.matches(request.getLlaveSecreta(), usuario.getHashContrasena())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Generar token JWT
        String token = jwtUtil.generarToken(usuario.getUuid());

        // Guardar token en sesiones_usuario (equivalente a guardar access_token)
        SesionUsuario sesion = new SesionUsuario();
        sesion.setUsuarioId(usuario.getId());
        sesion.setHashToken(token);
        sesion.setEstaActivo(true);
        sesion.setExpiraEn(LocalDateTime.now().plusYears(1)); // 1 año (TIMESTAMP MySQL max 2038)
        sesionRepository.save(sesion);

        // Actualizar ultimo acceso
        usuario.setUltimoAccesoEn(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return new TokenResponse(token, usuario.getUuid());
    }

    /**
     * Obtener negocio_id a partir del UUID del usuario autenticado.
     * Usado por los servicios del Bloque 5 para filtrar por tenant.
     * Equivalente a obtenerNegocioId(clienteId) de RegistroService.
     */
    public Long obtenerNegocioId(String uuid) {
        Usuario usuario = usuarioRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + uuid));
        return usuario.getNegocioId();
    }

    // ============================================================
    // CRUD COMPLETO PARA USUARIOS (GET, PUT, DELETE)
    // ============================================================

    /**
     * Listar todos los usuarios del negocio autenticado.
     */
    public List<Usuario> listarUsuarios(Long negocioId) {
        return usuarioRepository.findByNegocioId(negocioId);
    }

    /**
     * Obtener un usuario por ID, validando que pertenezca al negocio.
     */
    public Usuario obtenerUsuario(Long negocioId, Long id) {
        return usuarioRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    }

    /**
     * Actualizar un usuario existente.
     */
    @Transactional
    public Usuario actualizarUsuario(Long negocioId, Long id, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        if (request.getNombres() != null) {
            usuario.setNombres(request.getNombres());
        }
        if (request.getApellidos() != null) {
            usuario.setApellidos(request.getApellidos());
        }
        if (request.getEmail() != null) {
            // Verificar que el nuevo email no esté en uso por otro usuario
            if (!request.getEmail().equals(usuario.getEmail()) &&
                    usuarioRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("El email ya está en uso por otro usuario");
            }
            usuario.setEmail(request.getEmail());
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }
        if (request.getEstaActivo() != null) {
            usuario.setEstaActivo(request.getEstaActivo());
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Eliminar usuario (borrado lógico con @SQLDelete).
     */
    @Transactional
    public void eliminarUsuario(Long negocioId, Long id) {
        Usuario usuario = usuarioRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        usuarioRepository.delete(usuario);
    }
}
