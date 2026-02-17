package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.DetalleRecepcionCompraRequest;
import DrinkGo.DrinkGo_backend.dto.DetalleRecepcionCompraResponse;
import DrinkGo.DrinkGo_backend.entity.DetalleRecepcionCompra;
import DrinkGo.DrinkGo_backend.repository.DetalleRecepcionCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para DetalleRecepcionCompra
 */
@Service
public class DetalleRecepcionCompraService {

    @Autowired
    private DetalleRecepcionCompraRepository detalleRecepcionCompraRepository;

    @Transactional(readOnly = true)
    public List<DetalleRecepcionCompraResponse> obtenerTodosLosDetalles() {
        return detalleRecepcionCompraRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleRecepcionCompraResponse> obtenerDetallesPorRecepcion(Long recepcionId) {
        return detalleRecepcionCompraRepository.findByRecepcionId(recepcionId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleRecepcionCompraResponse> obtenerDetallesPorProducto(Long productoId) {
        return detalleRecepcionCompraRepository.findByProductoId(productoId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DetalleRecepcionCompraResponse obtenerDetallePorId(Long id) {
        DetalleRecepcionCompra detalle = detalleRecepcionCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de recepción no encontrado con ID: " + id));
        return convertirAResponse(detalle);
    }

    @Transactional
    public DetalleRecepcionCompraResponse crearDetalle(DetalleRecepcionCompraRequest request) {
        DetalleRecepcionCompra detalle = new DetalleRecepcionCompra();
        mapearRequestAEntidad(request, detalle);
        DetalleRecepcionCompra guardado = detalleRecepcionCompraRepository.save(detalle);
        return convertirAResponse(guardado);
    }

    @Transactional
    public DetalleRecepcionCompraResponse actualizarDetalle(Long id, DetalleRecepcionCompraRequest request) {
        DetalleRecepcionCompra detalle = detalleRecepcionCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de recepción no encontrado con ID: " + id));

        mapearRequestAEntidad(request, detalle);
        DetalleRecepcionCompra actualizado = detalleRecepcionCompraRepository.save(detalle);
        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminarDetalle(Long id) {
        if (!detalleRecepcionCompraRepository.existsById(id)) {
            throw new RuntimeException("Detalle de recepción no encontrado con ID: " + id);
        }
        detalleRecepcionCompraRepository.deleteById(id);
    }

    @Transactional
    public void eliminarDetallesPorRecepcion(Long recepcionId) {
        detalleRecepcionCompraRepository.deleteByRecepcionId(recepcionId);
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(DetalleRecepcionCompraRequest request, DetalleRecepcionCompra detalle) {
        detalle.setRecepcionId(request.getRecepcionId());
        detalle.setDetalleOrdenCompraId(request.getDetalleOrdenCompraId());
        detalle.setProductoId(request.getProductoId());
        detalle.setLoteId(request.getLoteId());
        detalle.setCantidadRecibida(request.getCantidadRecibida());
        detalle.setCantidadRechazada(request.getCantidadRechazada() != null ? request.getCantidadRechazada() : 0);
        detalle.setRazonRechazo(request.getRazonRechazo());
    }

    private DetalleRecepcionCompraResponse convertirAResponse(DetalleRecepcionCompra detalle) {
        DetalleRecepcionCompraResponse response = new DetalleRecepcionCompraResponse();
        response.setId(detalle.getId());
        response.setRecepcionId(detalle.getRecepcionId());
        response.setDetalleOrdenCompraId(detalle.getDetalleOrdenCompraId());
        response.setProductoId(detalle.getProductoId());
        response.setLoteId(detalle.getLoteId());
        response.setCantidadRecibida(detalle.getCantidadRecibida());
        response.setCantidadRechazada(detalle.getCantidadRechazada());
        response.setRazonRechazo(detalle.getRazonRechazo());
        return response;
    }
}
