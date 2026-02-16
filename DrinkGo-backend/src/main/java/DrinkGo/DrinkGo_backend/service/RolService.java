package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.RolDTO;
import DrinkGo.DrinkGo_backend.entity.Permiso;
import DrinkGo.DrinkGo_backend.entity.Rol;
import DrinkGo.DrinkGo_backend.repository.PermisoRepository;
import DrinkGo.DrinkGo_backend.repository.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RolService {
    
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    
    public RolService(RolRepository rolRepository, PermisoRepository permisoRepository) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Rol> findRolesDisponibles(Long tenantId) {
        return rolRepository.findRolesDisponibles(tenantId);
    }
    
    @Transactional(readOnly = true)
    public List<Rol> findRolesGlobales() {
        return rolRepository.findRolesGlobales();
    }
    
    @Transactional(readOnly = true)
    public List<Rol> findRolesPersonalizados(Long tenantId) {
        return rolRepository.findRolesPersonalizados(tenantId);
    }
    
    @Transactional(readOnly = true)
    public Rol findById(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public Rol findByCodigo(String codigo) {
        return rolRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + codigo));
    }
    
    @Transactional
    public Rol crear(RolDTO dto, Long tenantId) {
        if (rolRepository.existsByTenantIdAndCodigo(tenantId, dto.getCodigo())) {
            throw new RuntimeException("Ya existe un rol con el código: " + dto.getCodigo());
        }
        
        Rol rol = new Rol();
        rol.setTenantId(tenantId);
        rol.setCodigo(dto.getCodigo());
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        rol.setEsSistema(false); // Los roles creados por tenant no son de sistema
        
        if (dto.getPermisosIds() != null && !dto.getPermisosIds().isEmpty()) {
            Set<Permiso> permisos = new HashSet<>();
            for (Long permisoId : dto.getPermisosIds()) {
                permisoRepository.findById(permisoId).ifPresent(permisos::add);
            }
            rol.setPermisos(permisos);
        }
        
        return rolRepository.save(rol);
    }
    
    @Transactional
    public Rol actualizar(Long id, RolDTO dto, Long tenantId) {
        Rol rol = findById(id);
        
        // No se pueden editar roles de sistema
        if (rol.getEsSistema()) {
            throw new RuntimeException("No se pueden modificar roles del sistema");
        }
        
        // Verificar que el rol pertenece al tenant
        if (rol.getTenantId() != null && !rol.getTenantId().equals(tenantId)) {
            throw new RuntimeException("No tiene permiso para modificar este rol");
        }
        
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        
        if (dto.getPermisosIds() != null) {
            Set<Permiso> permisos = new HashSet<>();
            for (Long permisoId : dto.getPermisosIds()) {
                permisoRepository.findById(permisoId).ifPresent(permisos::add);
            }
            rol.setPermisos(permisos);
        }
        
        return rolRepository.save(rol);
    }
    
    @Transactional
    public Rol asignarPermisos(Long rolId, Set<Long> permisosIds, Long tenantId) {
        Rol rol = findById(rolId);
        
        if (rol.getEsSistema()) {
            throw new RuntimeException("No se pueden modificar permisos de roles del sistema");
        }
        
        Set<Permiso> permisos = new HashSet<>();
        for (Long permisoId : permisosIds) {
            permisoRepository.findById(permisoId).ifPresent(permisos::add);
        }
        
        rol.setPermisos(permisos);
        return rolRepository.save(rol);
    }
    
    @Transactional
    public void desactivar(Long id, Long tenantId) {
        Rol rol = findById(id);
        
        if (rol.getEsSistema()) {
            throw new RuntimeException("No se pueden desactivar roles del sistema");
        }
        
        if (rol.getTenantId() != null && !rol.getTenantId().equals(tenantId)) {
            throw new RuntimeException("No tiene permiso para desactivar este rol");
        }
        
        rol.setActivo(false);
        rolRepository.save(rol);
    }
    
    @Transactional(readOnly = true)
    public List<Permiso> findPermisos() {
        return permisoRepository.findByActivoTrue();
    }
    
    @Transactional(readOnly = true)
    public List<Permiso> findPermisosByRol(Long rolId) {
        return permisoRepository.findByRolId(rolId);
    }
}
