package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.AlmacenDTO;
import DrinkGo.DrinkGo_backend.entity.Almacen;
import DrinkGo.DrinkGo_backend.entity.Sede;
import DrinkGo.DrinkGo_backend.repository.AlmacenRepository;
import DrinkGo.DrinkGo_backend.repository.SedeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

public class AlmacenService {
    
    private final AlmacenRepository almacenRepository;
    private final SedeRepository sedeRepository;
    
    public AlmacenService(AlmacenRepository almacenRepository, SedeRepository sedeRepository) {
        this.almacenRepository = almacenRepository;
        this.sedeRepository = sedeRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Almacen> findBySede(Long sedeId) {
        return almacenRepository.findBySedeIdAndActivoTrue(sedeId);
    }
    
    @Transactional(readOnly = true)
    public List<Almacen> findByTenant(Long tenantId) {
        return almacenRepository.findByTenantIdAndActivoTrue(tenantId);
    }
    
    @Transactional(readOnly = true)
    public Almacen findById(Long id, Long tenantId) {
        return almacenRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Almacén no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public Almacen findAlmacenPrincipal(Long sedeId) {
        return almacenRepository.findAlmacenPrincipal(sedeId)
                .orElseThrow(() -> new RuntimeException("No hay almacén principal configurado"));
    }
    
    @Transactional
    public Almacen crear(AlmacenDTO dto, Long tenantId) {
        // Verificar que la sede existe y pertenece al tenant
        Sede sede = sedeRepository.findByIdAndTenantId(dto.getSedeId(), tenantId)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
        
        if (almacenRepository.existsBySedeIdAndCodigo(dto.getSedeId(), dto.getCodigo())) {
            throw new RuntimeException("Ya existe un almacén con el código: " + dto.getCodigo());
        }
        
        Almacen almacen = new Almacen();
        almacen.setTenantId(tenantId);
        almacen.setSedeId(dto.getSedeId());
        mapearDtoAEntidad(dto, almacen);
        
        Almacen saved = almacenRepository.save(almacen);
        
        // Si es principal, actualizar la sede
        if (Boolean.TRUE.equals(dto.getEsPrincipal())) {
            sede.setAlmacenPrincipalId(saved.getId());
            sedeRepository.save(sede);
        }
        
        return saved;
    }
    
    @Transactional
    public Almacen actualizar(Long id, AlmacenDTO dto, Long tenantId) {
        Almacen almacen = findById(id, tenantId);
        
        if (!almacen.getCodigo().equals(dto.getCodigo()) 
            && almacenRepository.existsBySedeIdAndCodigo(almacen.getSedeId(), dto.getCodigo())) {
            throw new RuntimeException("Ya existe un almacén con el código: " + dto.getCodigo());
        }
        
        mapearDtoAEntidad(dto, almacen);
        return almacenRepository.save(almacen);
    }
    
    @Transactional
    public void desactivar(Long id, Long tenantId) {
        Almacen almacen = findById(id, tenantId);
        almacen.setActivo(false);
        almacenRepository.save(almacen);
    }
    
    @Transactional(readOnly = true)
    public List<Almacen> findAlmacenesFrios(Long tenantId) {
        return almacenRepository.findAlmacenesFrios(tenantId);
    }
    
    private void mapearDtoAEntidad(AlmacenDTO dto, Almacen almacen) {
        almacen.setCodigo(dto.getCodigo());
        almacen.setNombre(dto.getNombre());
        almacen.setDescripcion(dto.getDescripcion());
        
        if (dto.getTipo() != null) almacen.setTipo(dto.getTipo());
        if (dto.getCapacidadUnidades() != null) almacen.setCapacidadUnidades(dto.getCapacidadUnidades());
        if (dto.getEsPrincipal() != null) almacen.setEsPrincipal(dto.getEsPrincipal());
        if (dto.getActivo() != null) almacen.setActivo(dto.getActivo());
    }
}
