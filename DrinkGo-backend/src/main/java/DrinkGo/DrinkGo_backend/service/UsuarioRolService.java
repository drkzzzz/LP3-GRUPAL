package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.UsuarioRolRequest;
import DrinkGo.DrinkGo_backend.dto.UsuarioRolResponse;
import DrinkGo.DrinkGo_backend.entity.UsuarioRol;
import DrinkGo.DrinkGo_backend.repository.UsuarioRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para UsuarioRol
 */
@Service
public class UsuarioRolService {

    @Autowired
    private UsuarioRolRepository usuarioRolRepository;

    @Transactional(readOnly = true)
    public List<UsuarioRolResponse> obtenerTodasLasAsignaciones() {
        return usuarioRolRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioRolResponse> obtenerRolesPorUsuario(Long usuarioId) {
        return usuarioRolRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioRolResponse> obtenerUsuariosPorRol(Long rolId) {
        return usuarioRolRepository.findByRolId(rolId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioRolResponse> obtenerAsignacionesVigentes() {
        return usuarioRolRepository.findByValidoHastaIsNullOrValidoHastaAfter(LocalDateTime.now()).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioRolResponse> obtenerRolesVigentesPorUsuario(Long usuarioId) {
        return usuarioRolRepository.findByUsuarioIdAndValidoHastaIsNullOrValidoHastaAfter(usuarioId, LocalDateTime.now()).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioRolResponse obtenerAsignacionPorId(Long id) {
        UsuarioRol usuarioRol = usuarioRolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada con ID: " + id));
        return convertirAResponse(usuarioRol);
    }

    @Transactional
    public UsuarioRolResponse asignarRolAUsuario(UsuarioRolRequest request) {
        if (usuarioRolRepository.existsByUsuarioIdAndRolId(request.getUsuarioId(), request.getRolId())) {
            throw new RuntimeException("El rol ya está asignado a este usuario");
        }

        UsuarioRol usuarioRol = new UsuarioRol();
        mapearRequestAEntidad(request, usuarioRol);
        UsuarioRol guardado = usuarioRolRepository.save(usuarioRol);
        return convertirAResponse(guardado);
    }

    @Transactional
    public UsuarioRolResponse actualizarAsignacion(Long id, UsuarioRolRequest request) {
        UsuarioRol usuarioRol = usuarioRolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada con ID: " + id));

        mapearRequestAEntidad(request, usuarioRol);
        UsuarioRol actualizado = usuarioRolRepository.save(usuarioRol);
        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminarAsignacion(Long id) {
        if (!usuarioRolRepository.existsById(id)) {
            throw new RuntimeException("Asignación no encontrada con ID: " + id);
        }
        usuarioRolRepository.deleteById(id);
    }

    @Transactional
    public void eliminarRolDeUsuario(Long usuarioId, Long rolId) {
        if (!usuarioRolRepository.existsByUsuarioIdAndRolId(usuarioId, rolId)) {
            throw new RuntimeException("No existe asignación para el usuario " + usuarioId + " y rol " + rolId);
        }
        usuarioRolRepository.deleteByUsuarioIdAndRolId(usuarioId, rolId);
    }

    @Transactional
    public void eliminarTodosLosRolesDelUsuario(Long usuarioId) {
        usuarioRolRepository.deleteByUsuarioId(usuarioId);
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(UsuarioRolRequest request, UsuarioRol usuarioRol) {
        usuarioRol.setUsuarioId(request.getUsuarioId());
        usuarioRol.setRolId(request.getRolId());
        usuarioRol.setAsignadoPor(request.getAsignadoPor());
        usuarioRol.setValidoHasta(request.getValidoHasta());
    }

    private UsuarioRolResponse convertirAResponse(UsuarioRol usuarioRol) {
        UsuarioRolResponse response = new UsuarioRolResponse();
        response.setId(usuarioRol.getId());
        response.setUsuarioId(usuarioRol.getUsuarioId());
        response.setRolId(usuarioRol.getRolId());
        response.setAsignadoPor(usuarioRol.getAsignadoPor());
        response.setAsignadoEn(usuarioRol.getAsignadoEn());
        response.setValidoHasta(usuarioRol.getValidoHasta());
        return response;
    }
}
