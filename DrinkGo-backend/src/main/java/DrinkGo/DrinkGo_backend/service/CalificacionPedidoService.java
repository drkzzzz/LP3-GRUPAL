package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.CalificacionPedidoDTO;
import DrinkGo.DrinkGo_backend.entity.CalificacionPedido;
import DrinkGo.DrinkGo_backend.entity.Pedido;
import DrinkGo.DrinkGo_backend.enums.OrderStatus;
import DrinkGo.DrinkGo_backend.repository.CalificacionPedidoRepository;
import DrinkGo.DrinkGo_backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CalificacionPedidoService {
    
    @Autowired
    private CalificacionPedidoRepository calificacionRepository;
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    // Crear calificación
    public CalificacionPedidoDTO crearCalificacion(Long tenantId, CalificacionPedidoDTO dto) {
        // Validar que el pedido existe y pertenece al tenant
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        // Validar que el pedido está entregado
        if (pedido.getEstado() != OrderStatus.entregado) {
            throw new RuntimeException("Solo se pueden calificar pedidos entregados");
        }
        
        // Validar que no existe calificación previa
        if (calificacionRepository.findByPedidoId(dto.getPedidoId()).isPresent()) {
            throw new RuntimeException("El pedido ya tiene una calificación");
        }
        
        // Validar estrellas (1-5)
        if (dto.getEstrellas() < 1 || dto.getEstrellas() > 5) {
            throw new RuntimeException("Las estrellas deben estar entre 1 y 5");
        }
        
        CalificacionPedido calificacion = new CalificacionPedido();
        calificacion.setPedidoId(dto.getPedidoId());
        calificacion.setEstrellas(dto.getEstrellas());
        calificacion.setComentario(dto.getComentario());
        calificacion.setPuntualidad(dto.getPuntualidad());
        calificacion.setCalidadProducto(dto.getCalidadProducto());
        calificacion.setAtencion(dto.getAtencion());
        
        calificacion = calificacionRepository.save(calificacion);
        return convertirADTO(calificacion);
    }
    
    // Obtener calificación de un pedido
    @Transactional(readOnly = true)
    public Optional<CalificacionPedidoDTO> obtenerCalificacionPedido(Long tenantId, Long pedidoId) {
        // Validar que el pedido pertenece al tenant
        pedidoRepository.findById(pedidoId)
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        return calificacionRepository.findByPedidoId(pedidoId)
            .map(this::convertirADTO);
    }
    
    // Obtener promedio de estrellas del tenant
    @Transactional(readOnly = true)
    public Double obtenerPromedioTenant(Long tenantId) {
        Double promedio = calificacionRepository.promedioEstrellasPorTenant(tenantId);
        return promedio != null ? promedio : 0.0;
    }
    
    // Obtener promedio de estrellas por sede
    @Transactional(readOnly = true)
    public Double obtenerPromedioSede(Long tenantId, Long sedeId) {
        Double promedio = calificacionRepository.promedioEstrellasPorSede(tenantId, sedeId);
        return promedio != null ? promedio : 0.0;
    }
    
    // Obtener promedio de puntualidad de un repartidor
    @Transactional(readOnly = true)
    public Double obtenerPromedioPuntualidadRepartidor(Long tenantId, Long repartidorId) {
        Double promedio = calificacionRepository.promedioPuntualidadRepartidor(tenantId, repartidorId);
        return promedio != null ? promedio : 0.0;
    }
    
    // Métodos privados
    private CalificacionPedidoDTO convertirADTO(CalificacionPedido calificacion) {
        CalificacionPedidoDTO dto = new CalificacionPedidoDTO();
        dto.setId(calificacion.getId());
        dto.setPedidoId(calificacion.getPedidoId());
        dto.setEstrellas(calificacion.getEstrellas());
        dto.setComentario(calificacion.getComentario());
        dto.setPuntualidad(calificacion.getPuntualidad());
        dto.setCalidadProducto(calificacion.getCalidadProducto());
        dto.setAtencion(calificacion.getAtencion());
        dto.setCreadoEn(calificacion.getCreadoEn());
        return dto;
    }
}
