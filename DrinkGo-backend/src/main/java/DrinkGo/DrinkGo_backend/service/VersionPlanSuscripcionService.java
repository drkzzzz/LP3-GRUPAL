package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.VersionPlanSuscripcionRequest;
import DrinkGo.DrinkGo_backend.dto.VersionPlanSuscripcionResponse;
import DrinkGo.DrinkGo_backend.entity.VersionPlanSuscripcion;
import DrinkGo.DrinkGo_backend.repository.VersionPlanSuscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para VersionPlanSuscripcion
 */
@Service
public class VersionPlanSuscripcionService {

    @Autowired
    private VersionPlanSuscripcionRepository versionPlanSuscripcionRepository;

    @Transactional(readOnly = true)
    public List<VersionPlanSuscripcionResponse> obtenerTodasLasVersiones() {
        return versionPlanSuscripcionRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VersionPlanSuscripcionResponse> obtenerVersionesPorPlanId(Long planId) {
        return versionPlanSuscripcionRepository.findByPlanIdOrderByVersionDesc(planId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VersionPlanSuscripcionResponse obtenerVersionPorId(Long id) {
        VersionPlanSuscripcion version = versionPlanSuscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Versión no encontrada con ID: " + id));
        return convertirAResponse(version);
    }

    @Transactional(readOnly = true)
    public VersionPlanSuscripcionResponse obtenerVersionEspecifica(Long planId, Integer numeroVersion) {
        VersionPlanSuscripcion version = versionPlanSuscripcionRepository.findByPlanIdAndVersion(planId, numeroVersion)
                .orElseThrow(() -> new RuntimeException("Versión " + numeroVersion + " no encontrada para el plan " + planId));
        return convertirAResponse(version);
    }

    @Transactional
    public VersionPlanSuscripcionResponse crearVersionPlan(VersionPlanSuscripcionRequest request) {
        VersionPlanSuscripcion version = new VersionPlanSuscripcion();
        mapearRequestAEntidad(request, version);
        VersionPlanSuscripcion guardada = versionPlanSuscripcionRepository.save(version);
        return convertirAResponse(guardada);
    }

    @Transactional
    public VersionPlanSuscripcionResponse actualizarVersion(Long id, VersionPlanSuscripcionRequest request) {
        VersionPlanSuscripcion version = versionPlanSuscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Versión no encontrada con ID: " + id));
        
        mapearRequestAEntidad(request, version);
        VersionPlanSuscripcion actualizada = versionPlanSuscripcionRepository.save(version);
        return convertirAResponse(actualizada);
    }

    @Transactional
    public void eliminarVersion(Long id) {
        if (!versionPlanSuscripcionRepository.existsById(id)) {
            throw new RuntimeException("Versión no encontrada con ID: " + id);
        }
        versionPlanSuscripcionRepository.deleteById(id);
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(VersionPlanSuscripcionRequest request, VersionPlanSuscripcion version) {
        version.setPlanId(request.getPlanId());
        version.setVersion(request.getVersion());
        version.setSnapshotJson(request.getSnapshotJson());
        version.setCambiadoPor(request.getCambiadoPor());
        version.setRazonCambio(request.getRazonCambio());
    }

    private VersionPlanSuscripcionResponse convertirAResponse(VersionPlanSuscripcion version) {
        VersionPlanSuscripcionResponse response = new VersionPlanSuscripcionResponse();
        response.setId(version.getId());
        response.setPlanId(version.getPlanId());
        response.setVersion(version.getVersion());
        response.setSnapshotJson(version.getSnapshotJson());
        response.setCambiadoPor(version.getCambiadoPor());
        response.setRazonCambio(version.getRazonCambio());
        response.setCreadoEn(version.getCreadoEn());
        return response;
    }
}
