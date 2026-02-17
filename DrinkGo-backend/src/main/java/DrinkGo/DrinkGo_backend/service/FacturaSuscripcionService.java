package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.FacturaSuscripcionRequest;
import DrinkGo.DrinkGo_backend.dto.FacturaSuscripcionResponse;
import DrinkGo.DrinkGo_backend.entity.FacturaSuscripcion;
import DrinkGo.DrinkGo_backend.repository.FacturaSuscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para FacturaSuscripcion
 */
@Service
public class FacturaSuscripcionService {

    @Autowired
    private FacturaSuscripcionRepository facturaSuscripcionRepository;

    @Transactional(readOnly = true)
    public List<FacturaSuscripcionResponse> obtenerTodasLasFacturas() {
        return facturaSuscripcionRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FacturaSuscripcionResponse> obtenerFacturasPorNegocio(Long negocioId) {
        return facturaSuscripcionRepository.findByNegocioId(negocioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FacturaSuscripcionResponse> obtenerFacturasPorSuscripcion(Long suscripcionId) {
        return facturaSuscripcionRepository.findBySuscripcionId(suscripcionId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FacturaSuscripcionResponse obtenerFacturaPorId(Long id) {
        FacturaSuscripcion factura = facturaSuscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
        return convertirAResponse(factura);
    }

    @Transactional(readOnly = true)
    public FacturaSuscripcionResponse obtenerFacturaPorNumero(String numeroFactura) {
        FacturaSuscripcion factura = facturaSuscripcionRepository.findByNumeroFactura(numeroFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con número: " + numeroFactura));
        return convertirAResponse(factura);
    }

    @Transactional
    public FacturaSuscripcionResponse crearFacturaSuscripcion(FacturaSuscripcionRequest request) {
        if (facturaSuscripcionRepository.existsByNumeroFactura(request.getNumeroFactura())) {
            throw new RuntimeException("Ya existe una factura con el número: " + request.getNumeroFactura());
        }

        FacturaSuscripcion factura = new FacturaSuscripcion();
        mapearRequestAEntidad(request, factura);
        FacturaSuscripcion guardada = facturaSuscripcionRepository.save(factura);
        return convertirAResponse(guardada);
    }

    @Transactional
    public FacturaSuscripcionResponse actualizarFacturaSuscripcion(Long id, FacturaSuscripcionRequest request) {
        FacturaSuscripcion factura = facturaSuscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));

        mapearRequestAEntidad(request, factura);
        FacturaSuscripcion actualizada = facturaSuscripcionRepository.save(factura);
        return convertirAResponse(actualizada);
    }

    @Transactional
    public FacturaSuscripcionResponse marcarComoPagada(Long id, String metodoPago, String referenciaPago) {
        FacturaSuscripcion factura = facturaSuscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));

        factura.setEstado(FacturaSuscripcion.EstadoFactura.pagada);
        factura.setMetodoPago(metodoPago);
        factura.setReferenciaPago(referenciaPago);
        factura.setPagadoEn(LocalDateTime.now());

        FacturaSuscripcion actualizada = facturaSuscripcionRepository.save(factura);
        return convertirAResponse(actualizada);
    }

    @Transactional
    public void eliminarFactura(Long id) {
        if (!facturaSuscripcionRepository.existsById(id)) {
            throw new RuntimeException("Factura no encontrada con ID: " + id);
        }
        facturaSuscripcionRepository.deleteById(id);
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(FacturaSuscripcionRequest request, FacturaSuscripcion factura) {
        factura.setSuscripcionId(request.getSuscripcionId());
        factura.setNegocioId(request.getNegocioId());
        factura.setNumeroFactura(request.getNumeroFactura());
        factura.setInicioPeriodo(request.getInicioPeriodo());
        factura.setFinPeriodo(request.getFinPeriodo());
        factura.setSubtotal(request.getSubtotal());
        factura.setMontoImpuesto(request.getMontoImpuesto());
        factura.setMontoDescuento(request.getMontoDescuento());
        factura.setTotal(request.getTotal());
        factura.setMoneda(request.getMoneda() != null ? request.getMoneda() : "PEN");
        
        if (request.getEstado() != null) {
            factura.setEstado(FacturaSuscripcion.EstadoFactura.valueOf(request.getEstado()));
        }
        
        factura.setMetodoPago(request.getMetodoPago());
        factura.setReferenciaPago(request.getReferenciaPago());
        factura.setNotas(request.getNotas());
        factura.setFechaVencimiento(request.getFechaVencimiento());
    }

    private FacturaSuscripcionResponse convertirAResponse(FacturaSuscripcion factura) {
        FacturaSuscripcionResponse response = new FacturaSuscripcionResponse();
        response.setId(factura.getId());
        response.setSuscripcionId(factura.getSuscripcionId());
        response.setNegocioId(factura.getNegocioId());
        response.setNumeroFactura(factura.getNumeroFactura());
        response.setInicioPeriodo(factura.getInicioPeriodo());
        response.setFinPeriodo(factura.getFinPeriodo());
        response.setSubtotal(factura.getSubtotal());
        response.setMontoImpuesto(factura.getMontoImpuesto());
        response.setMontoDescuento(factura.getMontoDescuento());
        response.setTotal(factura.getTotal());
        response.setMoneda(factura.getMoneda());
        response.setEstado(factura.getEstado() != null ? factura.getEstado().name() : null);
        response.setMetodoPago(factura.getMetodoPago());
        response.setReferenciaPago(factura.getReferenciaPago());
        response.setPagadoEn(factura.getPagadoEn());
        response.setIntentosReintento(factura.getIntentosReintento());
        response.setProximoReintentoEn(factura.getProximoReintentoEn());
        response.setNotas(factura.getNotas());
        response.setEmitidoEn(factura.getEmitidoEn());
        response.setFechaVencimiento(factura.getFechaVencimiento());
        response.setCreadoEn(factura.getCreadoEn());
        response.setActualizadoEn(factura.getActualizadoEn());
        return response;
    }
}
