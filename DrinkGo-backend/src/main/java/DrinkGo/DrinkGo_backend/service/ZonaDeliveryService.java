package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.ZonaDeliveryDTO;
import DrinkGo.DrinkGo_backend.entity.ZonaDelivery;
import DrinkGo.DrinkGo_backend.repository.ZonaDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ZonaDeliveryService {
    
    @Autowired
    private ZonaDeliveryRepository zonaDeliveryRepository;
    
    // Crear zona de delivery
    public ZonaDeliveryDTO crearZona(Long tenantId, ZonaDeliveryDTO dto) {
        ZonaDelivery zona = new ZonaDelivery();
        zona.setTenantId(tenantId);
        mapearDatos(zona, dto);
        zona = zonaDeliveryRepository.save(zona);
        return convertirADTO(zona);
    }
    
    // Obtener zona por ID
    @Transactional(readOnly = true)
    public ZonaDeliveryDTO obtenerZona(Long tenantId, Long id) {
        ZonaDelivery zona = zonaDeliveryRepository.findById(id)
            .filter(z -> z.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Zona de delivery no encontrada"));
        return convertirADTO(zona);
    }
    
    // Listar zonas por sede
    @Transactional(readOnly = true)
    public List<ZonaDeliveryDTO> listarZonasPorSede(Long tenantId, Long sedeId) {
        return zonaDeliveryRepository.findByTenantIdAndSedeId(tenantId, sedeId)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    // Listar zonas activas por sede
    @Transactional(readOnly = true)
    public List<ZonaDeliveryDTO> listarZonasActivasPorSede(Long tenantId, Long sedeId) {
        return zonaDeliveryRepository.findByTenantIdAndSedeIdAndActivoTrue(tenantId, sedeId)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    // Buscar zona por distrito
    @Transactional(readOnly = true)
    public ZonaDeliveryDTO buscarZonaPorDistrito(Long tenantId, Long sedeId, String distrito) {
        ZonaDelivery zona = zonaDeliveryRepository.findByDistrito(tenantId, sedeId, distrito)
            .orElseThrow(() -> new RuntimeException("No hay cobertura de delivery para el distrito: " + distrito));
        return convertirADTO(zona);
    }
    
    // Actualizar zona
    public ZonaDeliveryDTO actualizarZona(Long tenantId, Long id, ZonaDeliveryDTO dto) {
        ZonaDelivery zona = zonaDeliveryRepository.findById(id)
            .filter(z -> z.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Zona de delivery no encontrada"));
        
        mapearDatos(zona, dto);
        zona = zonaDeliveryRepository.save(zona);
        return convertirADTO(zona);
    }
    
    // Activar/Desactivar zona
    public ZonaDeliveryDTO cambiarEstadoZona(Long tenantId, Long id, Boolean activo) {
        ZonaDelivery zona = zonaDeliveryRepository.findById(id)
            .filter(z -> z.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Zona de delivery no encontrada"));
        
        zona.setActivo(activo);
        zona = zonaDeliveryRepository.save(zona);
        return convertirADTO(zona);
    }
    
    // Eliminar zona
    public void eliminarZona(Long tenantId, Long id) {
        ZonaDelivery zona = zonaDeliveryRepository.findById(id)
            .filter(z -> z.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Zona de delivery no encontrada"));
        
        zonaDeliveryRepository.delete(zona);
    }
    
    // Métodos privados
    private void mapearDatos(ZonaDelivery zona, ZonaDeliveryDTO dto) {
        zona.setSedeId(dto.getSedeId());
        zona.setNombre(dto.getNombre());
        zona.setDistritos(dto.getDistritos());
        zona.setCostoDelivery(dto.getCostoDelivery());
        zona.setTiempoEstimadoMinutos(dto.getTiempoEstimadoMinutos());
        zona.setPedidoMinimo(dto.getPedidoMinimo());
        zona.setPoligono(dto.getPoligono());
        if (dto.getActivo() != null) {
            zona.setActivo(dto.getActivo());
        }
    }
    
    private ZonaDeliveryDTO convertirADTO(ZonaDelivery zona) {
        ZonaDeliveryDTO dto = new ZonaDeliveryDTO();
        dto.setId(zona.getId());
        dto.setSedeId(zona.getSedeId());
        dto.setNombre(zona.getNombre());
        dto.setDistritos(zona.getDistritos());
        dto.setCostoDelivery(zona.getCostoDelivery());
        dto.setTiempoEstimadoMinutos(zona.getTiempoEstimadoMinutos());
        dto.setPedidoMinimo(zona.getPedidoMinimo());
        dto.setPoligono(zona.getPoligono());
        dto.setActivo(zona.getActivo());
        return dto;
    }
}
