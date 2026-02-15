package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.SedeDTO;
import DrinkGo.DrinkGo_backend.entity.Sede;
import DrinkGo.DrinkGo_backend.entity.SedeConfig;
import DrinkGo.DrinkGo_backend.repository.SedeRepository;
import DrinkGo.DrinkGo_backend.repository.SedeConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SedeService {
    
    private final SedeRepository sedeRepository;
    private final SedeConfigRepository sedeConfigRepository;
    
    public SedeService(SedeRepository sedeRepository, SedeConfigRepository sedeConfigRepository) {
        this.sedeRepository = sedeRepository;
        this.sedeConfigRepository = sedeConfigRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Sede> findByTenant(Long tenantId) {
        return sedeRepository.findByTenantIdAndActivoTrue(tenantId);
    }
    
    @Transactional(readOnly = true)
    public Sede findById(Long id, Long tenantId) {
        return sedeRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
    }
    
    @Transactional(readOnly = true)
    public List<Sede> findSedesConMesas(Long tenantId) {
        return sedeRepository.findSedesConMesas(tenantId);
    }
    
    @Transactional(readOnly = true)
    public List<Sede> findSedesConDelivery(Long tenantId) {
        return sedeRepository.findSedesConDelivery(tenantId);
    }
    
    @Transactional
    public Sede crear(SedeDTO dto, Long tenantId) {
        if (sedeRepository.existsByTenantIdAndCodigo(tenantId, dto.getCodigo())) {
            throw new RuntimeException("Ya existe una sede con el código: " + dto.getCodigo());
        }
        
        Sede sede = new Sede();
        sede.setTenantId(tenantId);
        mapearDtoAEntidad(dto, sede);
        
        Sede sedeSaved = sedeRepository.save(sede);
        
        // Crear configuración por defecto
        SedeConfig config = new SedeConfig();
        config.setSedeId(sedeSaved.getId());
        config.setSede(sedeSaved);
        config.setTenantId(tenantId);
        sedeConfigRepository.save(config);
        
        return sedeSaved;
    }
    
    @Transactional
    public Sede actualizar(Long id, SedeDTO dto, Long tenantId) {
        Sede sede = findById(id, tenantId);
        
        // Verificar código único si cambió
        if (!sede.getCodigo().equals(dto.getCodigo()) 
            && sedeRepository.existsByTenantIdAndCodigo(tenantId, dto.getCodigo())) {
            throw new RuntimeException("Ya existe una sede con el código: " + dto.getCodigo());
        }
        
        mapearDtoAEntidad(dto, sede);
        return sedeRepository.save(sede);
    }
    
    @Transactional
    public void desactivar(Long id, Long tenantId) {
        Sede sede = findById(id, tenantId);
        sede.setActivo(false);
        sedeRepository.save(sede);
    }
    
    @Transactional(readOnly = true)
    public SedeConfig getConfiguracion(Long sedeId) {
        return sedeConfigRepository.findBySedeId(sedeId)
                .orElseThrow(() -> new RuntimeException("Configuración de sede no encontrada"));
    }
    
    @Transactional
    public SedeConfig actualizarConfiguracion(Long sedeId, SedeConfig config) {
        SedeConfig existing = getConfiguracion(sedeId);
        
        existing.setHoraApertura(config.getHoraApertura());
        existing.setHoraCierre(config.getHoraCierre());
        existing.setHoraInicioVentaAlcohol(config.getHoraInicioVentaAlcohol());
        existing.setHoraFinVentaAlcohol(config.getHoraFinVentaAlcohol());
        existing.setDiasSinVentaAlcohol(config.getDiasSinVentaAlcohol());
        existing.setLeySecaActiva(config.getLeySecaActiva());
        existing.setDeliveryRadioKm(config.getDeliveryRadioKm());
        existing.setDeliveryCostoBase(config.getDeliveryCostoBase());
        existing.setDeliveryCostoPorKm(config.getDeliveryCostoPorKm());
        existing.setDeliveryPedidoMinimo(config.getDeliveryPedidoMinimo());
        
        return sedeConfigRepository.save(existing);
    }
    
    private void mapearDtoAEntidad(SedeDTO dto, Sede sede) {
        sede.setCodigo(dto.getCodigo());
        sede.setNombre(dto.getNombre());
        sede.setDireccion(dto.getDireccion());
        sede.setDistrito(dto.getDistrito());
        sede.setCiudad(dto.getCiudad());
        sede.setTelefono(dto.getTelefono());
        sede.setEmail(dto.getEmail());
        sede.setCoordenadasLat(dto.getCoordenadasLat());
        sede.setCoordenadasLng(dto.getCoordenadasLng());
        
        if (dto.getHasTables() != null) sede.setHasTables(dto.getHasTables());
        if (dto.getHasDelivery() != null) sede.setHasDelivery(dto.getHasDelivery());
        if (dto.getHasPickup() != null) sede.setHasPickup(dto.getHasPickup());
        if (dto.getCapacidadMesas() != null) sede.setCapacidadMesas(dto.getCapacidadMesas());
        if (dto.getActivo() != null) sede.setActivo(dto.getActivo());
    }
}
