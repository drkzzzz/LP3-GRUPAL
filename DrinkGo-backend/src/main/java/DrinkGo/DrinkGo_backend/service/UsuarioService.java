package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.UsuarioDTO;
import DrinkGo.DrinkGo_backend.entity.Rol;
import DrinkGo.DrinkGo_backend.entity.Sede;
import DrinkGo.DrinkGo_backend.entity.Usuario;
import DrinkGo.DrinkGo_backend.repository.RolRepository;
import DrinkGo.DrinkGo_backend.repository.SedeRepository;
import DrinkGo.DrinkGo_backend.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final SedeRepository sedeRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                          SedeRepository sedeRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.sedeRepository = sedeRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional(readOnly = true)
    public Page<Usuario> findByTenant(Long tenantId, Pageable pageable) {
        return usuarioRepository.findByTenantIdAndActivoTrue(tenantId, pageable);
    }
    
    @Transactional(readOnly = true)
    public Usuario findById(Long id, Long tenantId) {
        return usuarioRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> buscar(Long tenantId, String busqueda) {
        return usuarioRepository.buscarUsuarios(tenantId, busqueda);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> findByRol(Long tenantId, String rolCodigo) {
        return usuarioRepository.findByTenantIdAndRol(tenantId, rolCodigo);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> findBySede(Long sedeId) {
        return usuarioRepository.findBySedeId(sedeId);
    }
    
    @Transactional
    public Usuario crear(UsuarioDTO dto, Long tenantId) {
        if (usuarioRepository.existsByTenantIdAndEmail(tenantId, dto.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
        }
        
        Usuario usuario = new Usuario();
        usuario.setTenantId(tenantId);
        usuario.setEmail(dto.getEmail());
        usuario.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        
        mapearDtoAEntidad(dto, usuario, tenantId);
        
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public Usuario actualizar(Long id, UsuarioDTO dto, Long tenantId) {
        Usuario usuario = findById(id, tenantId);
        
        // Verificar email único si cambió
        if (!usuario.getEmail().equals(dto.getEmail()) 
            && usuarioRepository.existsByTenantIdAndEmail(tenantId, dto.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
        }
        
        usuario.setEmail(dto.getEmail());
        
        // Actualizar contraseña solo si se proporciona
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            usuario.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        }
        
        mapearDtoAEntidad(dto, usuario, tenantId);
        
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void cambiarContrasena(Long id, String contrasenaActual, String contrasenaNueva, Long tenantId) {
        Usuario usuario = findById(id, tenantId);
        
        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasenaHash())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }
        
        usuario.setContrasenaHash(passwordEncoder.encode(contrasenaNueva));
        usuarioRepository.save(usuario);
    }
    
    @Transactional
    public Usuario asignarRoles(Long id, Set<Long> rolesIds, Long tenantId) {
        Usuario usuario = findById(id, tenantId);
        
        Set<Rol> roles = new HashSet<>();
        for (Long rolId : rolesIds) {
            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolId));
            // Verificar que el rol es válido para el tenant
            if (rol.getTenantId() != null && !rol.getTenantId().equals(tenantId)) {
                throw new RuntimeException("Rol no válido para este negocio");
            }
            roles.add(rol);
        }
        
        usuario.setRoles(roles);
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public Usuario asignarSedes(Long id, Set<Long> sedesIds, Long tenantId) {
        Usuario usuario = findById(id, tenantId);
        
        Set<Sede> sedes = new HashSet<>();
        for (Long sedeId : sedesIds) {
            Sede sede = sedeRepository.findByIdAndTenantId(sedeId, tenantId)
                    .orElseThrow(() -> new RuntimeException("Sede no encontrada: " + sedeId));
            sedes.add(sede);
        }
        
        usuario.setSedes(sedes);
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void desactivar(Long id, Long tenantId) {
        Usuario usuario = findById(id, tenantId);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void actualizarUltimoAcceso(Long id) {
        usuarioRepository.actualizarUltimoAcceso(id, OffsetDateTime.now());
    }
    
    @Transactional(readOnly = true)
    public Usuario loginPorPin(Long tenantId, String pin) {
        return usuarioRepository.findByPinRapido(tenantId, pin)
                .orElseThrow(() -> new RuntimeException("PIN inválido"));
    }
    
    private void mapearDtoAEntidad(UsuarioDTO dto, Usuario usuario, Long tenantId) {
        usuario.setCodigoEmpleado(dto.getCodigoEmpleado());
        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setTelefono(dto.getTelefono());
        usuario.setAvatarUrl(dto.getAvatarUrl());
        usuario.setSedePreferidaId(dto.getSedePreferidaId());
        usuario.setPinRapido(dto.getPinRapido());
        
        if (dto.getActivo() != null) usuario.setActivo(dto.getActivo());
        
        // Asignar roles si se proporcionan
        if (dto.getRolesIds() != null && !dto.getRolesIds().isEmpty()) {
            Set<Rol> roles = new HashSet<>();
            for (Long rolId : dto.getRolesIds()) {
                rolRepository.findById(rolId).ifPresent(roles::add);
            }
            usuario.setRoles(roles);
        }
        
        // Asignar sedes si se proporcionan
        if (dto.getSedesIds() != null && !dto.getSedesIds().isEmpty()) {
            Set<Sede> sedes = new HashSet<>();
            for (Long sedeId : dto.getSedesIds()) {
                sedeRepository.findByIdAndTenantId(sedeId, tenantId).ifPresent(sedes::add);
            }
            usuario.setSedes(sedes);
        }
    }
}
