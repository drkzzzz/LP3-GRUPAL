package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.*;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.repository.*;
import DrinkGo.DrinkGo_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SesionUsuarioRepository sesionRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ============================================================
    // AUTENTICACIÓN Y SEGURIDAD
    // ============================================================

    @Transactional
    public Usuario registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(request.getEmail());
        usuario.setNegocioId(request.getNegocioId());
        
        // Encriptar contraseña
        usuario.setHashContrasena(request.getLlaveSecreta());

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public TokenResponse generarToken(TokenRequest request) {
        Usuario usuario = usuarioRepository.findByUuid(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getEstaActivo()) {
            throw new RuntimeException("El usuario está desactivado");
        }

        if (!passwordEncoder.matches(request.getLlaveSecreta(), usuario.getHashContrasena())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generarToken(usuario.getUuid());

        // Guardar sesión
        SesionUsuario sesion = new SesionUsuario();
        sesion.setUsuarioId(usuario.getId());
        sesion.setHashToken(token);
        sesion.setEstaActivo(true);
        sesion.setExpiraEn(LocalDateTime.now().plusYears(1));
        sesionRepository.save(sesion);

        // Actualizar último acceso
        usuario.setUltimoAccesoEn(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return new TokenResponse(token, usuario.getUuid());
    }

    // ============================================================
    // GESTIÓN DE USUARIOS (CRUD + ROLES/SEDES)
    // ============================================================

    @Transactional(readOnly = true)
    public Page<Usuario> listarPaginado(Long negocioId, Pageable pageable) {
        return usuarioRepository.findByNegocioIdAndEstaActivoTrue(negocioId, pageable);
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id, Long negocioId) {
        return usuarioRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscar(Long negocioId, String busqueda) {
        return usuarioRepository.buscarUsuarios(negocioId, busqueda);
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, Long negocioId, UsuarioUpdateRequest request) {
        Usuario usuario = obtenerPorId(id, negocioId);

        if (request.getEmail() != null && !request.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("El email ya está en uso");
            }
            usuario.setEmail(request.getEmail());
        }

        if (request.getNombres() != null) usuario.setNombres(request.getNombres());
        if (request.getApellidos() != null) usuario.setApellidos(request.getApellidos());
        if (request.getTelefono() != null) usuario.setTelefono(request.getTelefono());
        if (request.getEstaActivo() != null) usuario.setEstaActivo(request.getEstaActivo());

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario asignarRolesYSedes(Long id, Long negocioId, Set<Long> rolesIds, Set<Long> sedesIds) {
        Usuario usuario = obtenerPorId(id, negocioId);

        if (rolesIds != null) {
            Set<Rol> roles = new HashSet<>();
            for (Long rolId : rolesIds) {
                rolRepository.findById(rolId).ifPresent(roles::add);
            }
            usuario.setRoles(roles);
        }

        if (sedesIds != null) {
            Set<Sede> sedes = new HashSet<>();
            for (Long sedeId : sedesIds) {
                sedeRepository.findByIdAndNegocioId(sedeId, negocioId).ifPresent(sedes::add);
            }
            usuario.setSedes(sedes);
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Long id, Long negocioId) {
        Usuario usuario = obtenerPorId(id, negocioId);
        usuarioRepository.delete(usuario); // Borrado lógico vía @SQLDelete
    }

    public Long obtenerNegocioId(String uuid) {
        return usuarioRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("UUID no válido"))
                .getNegocioId();
    }
}