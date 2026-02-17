package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.HistorialConfiguracionPlataformaRequest;
import DrinkGo.DrinkGo_backend.dto.HistorialConfiguracionPlataformaResponse;
import DrinkGo.DrinkGo_backend.entity.HistorialConfiguracionPlataforma;
import DrinkGo.DrinkGo_backend.repository.HistorialConfiguracionPlataformaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para HistorialConfiguracionPlataforma
 */
@Service
public class HistorialConfiguracionPlataformaService {

    @Autowired
    private HistorialConfiguracionPlataformaRepository historialConfiguracionPlataformaRepository;

    @Transactional(readOnly = true)
    public List<HistorialConfiguracionPlataformaResponse> obtenerTodoElHistorial() {
        return historialConfiguracionPlataformaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HistorialConfiguracionPlataformaResponse> obtenerHistorialPorConfiguracion(Long configuracionId) {
        return historialConfiguracionPlataformaRepository.findByConfiguracionIdOrderByCreadoEnDesc(configuracionId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HistorialConfiguracionPlataformaResponse obtenerHistorialPorId(Long id) {
        HistorialConfiguracionPlataforma historial = historialConfiguracionPlataformaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historial no encontrado con ID: " + id));
        return convertirAResponse(historial);
    }

    @Transactional
    public HistorialConfiguracionPlataformaResponse crearHistorial(HistorialConfiguracionPlataformaRequest request) {
        HistorialConfiguracionPlataforma historial = new HistorialConfiguracionPlataforma();
        mapearRequestAEntidad(request, historial);
        HistorialConfiguracionPlataforma guardado = historialConfiguracionPlataformaRepository.save(historial);
        return convertirAResponse(guardado);
    }

    @Transactional
    public HistorialConfiguracionPlataformaResponse actualizarHistorial(Long id, HistorialConfiguracionPlataformaRequest request) {
        HistorialConfiguracionPlataforma historial = historialConfiguracionPlataformaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historial no encontrado con ID: " + id));
        
        mapearRequestAEntidad(request, historial);
        HistorialConfiguracionPlataforma actualizado = historialConfiguracionPlataformaRepository.save(historial);
        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminarHistorial(Long id) {
        if (!historialConfiguracionPlataformaRepository.existsById(id)) {
            throw new RuntimeException("Historial no encontrado con ID: " + id);
        }
        historialConfiguracionPlataformaRepository.deleteById(id);
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(HistorialConfiguracionPlataformaRequest request, HistorialConfiguracionPlataforma historial) {
        historial.setConfiguracionId(request.getConfiguracionId());
        historial.setValorAnterior(request.getValorAnterior());
        historial.setValorNuevo(request.getValorNuevo());
        historial.setCambiadoPor(request.getCambiadoPor());
        historial.setRazonCambio(request.getRazonCambio());
    }

    private HistorialConfiguracionPlataformaResponse convertirAResponse(HistorialConfiguracionPlataforma historial) {
        HistorialConfiguracionPlataformaResponse response = new HistorialConfiguracionPlataformaResponse();
        response.setId(historial.getId());
        response.setConfiguracionId(historial.getConfiguracionId());
        response.setValorAnterior(historial.getValorAnterior());
        response.setValorNuevo(historial.getValorNuevo());
        response.setCambiadoPor(historial.getCambiadoPor());
        response.setRazonCambio(historial.getRazonCambio());
        response.setCreadoEn(historial.getCreadoEn());
        return response;
    }
}
