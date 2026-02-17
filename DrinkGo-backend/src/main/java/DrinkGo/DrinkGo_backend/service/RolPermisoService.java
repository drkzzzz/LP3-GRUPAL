package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.RolPermisoRequest;
import DrinkGo.DrinkGo_backend.dto.RolPermisoResponse;
import DrinkGo.DrinkGo_backend.entity.RolPermiso;
import DrinkGo.DrinkGo_backend.repository.RolPermisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para RolPermiso
 */
@Service
public class RolPermisoService {

    @Autowired
    private RolPermisoRepository rolPermisoRepository;

    @Transactional(readOnly = true)
    public List<RolPermisoResponse> obtenerTodasLasAsignaciones() {
        return rolPermisoRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RolPermisoResponse> obtenerPermisosPorRol(Long rolId) {
        return rolPermisoRepository.findByRolId(rolId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RolPermisoResponse> obtenerRolesPorPermiso(Long permisoId) {
        return rolPermisoRepository.findByPermisoId(permisoId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RolPermisoResponse obtenerAsignacionPorId(Long id) {
        RolPermiso rolPermiso = rolPermisoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada con ID: " + id));
        return convertirAResponse(rolPermiso);
    }

    @Transactional
    public RolPermisoResponse asignarPermisoARol(RolPermisoRequest request) {
        if (rolPermisoRepository.existsByRolIdAndPermisoId(request.getRolId(), request.getPermisoId())) {
            throw new RuntimeException("El permiso ya está asignado a este rol");
        }

        RolPermiso rolPermiso = new RolPermiso();
        mapearRequestAEntidad(request, rolPermiso);
        RolPermiso guardado = rolPermisoRepository.save(rolPermiso);
        return convertirAResponse(guardado);
    }

    @Transactional
    public RolPermisoResponse actualizarAsignacion(Long id, RolPermisoRequest request) {
        RolPermiso rolPermiso = rolPermisoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada con ID: " + id));

        mapearRequestAEntidad(request, rolPermiso);
        RolPermiso actualizado = rolPermisoRepository.save(rolPermiso);
        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminarAsignacion(Long id) {
        if (!rolPermisoRepository.existsById(id)) {
            throw new RuntimeException("Asignación no encontrada con ID: " + id);
        }
        rolPermisoRepository.deleteById(id);
    }

    @Transactional
    public void eliminarPermisoDeRol(Long rolId, Long permisoId) {
        if (!rolPermisoRepository.existsByRolIdAndPermisoId(rolId, permisoId)) {
            throw new RuntimeException("No existe asignación para el rol " + rolId + " y permiso " + permisoId);
        }
        rolPermisoRepository.deleteByRolIdAndPermisoId(rolId, permisoId);
    }

    @Transactional
    public void eliminarTodosLosPermisosDelRol(Long rolId) {
        rolPermisoRepository.deleteByRolId(rolId);
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(RolPermisoRequest request, RolPermiso rolPermiso) {
        rolPermiso.setRolId(request.getRolId());
        rolPermiso.setPermisoId(request.getPermisoId());
    }

    private RolPermisoResponse convertirAResponse(RolPermiso rolPermiso) {
        RolPermisoResponse response = new RolPermisoResponse();
        response.setId(rolPermiso.getId());
        response.setRolId(rolPermiso.getRolId());
        response.setPermisoId(rolPermiso.getPermisoId());
        response.setCreadoEn(rolPermiso.getCreadoEn());
        return response;
    }
}
