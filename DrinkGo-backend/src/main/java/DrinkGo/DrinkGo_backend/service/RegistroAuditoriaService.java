package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.RegistroAuditoriaRequest;
import DrinkGo.DrinkGo_backend.dto.RegistroAuditoriaResponse;
import DrinkGo.DrinkGo_backend.entity.RegistroAuditoria;
import DrinkGo.DrinkGo_backend.repository.RegistroAuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para RegistroAuditoria
 */
@Service
public class RegistroAuditoriaService {

    @Autowired
    private RegistroAuditoriaRepository registroAuditoriaRepository;

    @Transactional(readOnly = true)
    public List<RegistroAuditoriaResponse> obtenerTodosLosRegistros() {
        return registroAuditoriaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistroAuditoriaResponse> obtenerRegistrosPorNegocio(Long negocioId) {
        return registroAuditoriaRepository.findByNegocioId(negocioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistroAuditoriaResponse> obtenerRegistrosPorUsuario(Long usuarioId) {
        return registroAuditoriaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistroAuditoriaResponse> obtenerRegistrosPorAccion(String accion) {
        return registroAuditoriaRepository.findByAccion(accion).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistroAuditoriaResponse> obtenerRegistrosPorTipoEntidad(String tipoEntidad) {
        return registroAuditoriaRepository.findByTipoEntidad(tipoEntidad).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistroAuditoriaResponse> obtenerRegistrosPorEntidad(String tipoEntidad, Long entidadId) {
        return registroAuditoriaRepository.findByTipoEntidadAndEntidadId(tipoEntidad, entidadId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistroAuditoriaResponse> obtenerRegistrosPorRangoFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return registroAuditoriaRepository.findByFechaAccionBetween(fechaInicio, fechaFin).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistroAuditoriaResponse> obtenerRegistrosPorNegocioYFecha(Long negocioId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return registroAuditoriaRepository.findByNegocioIdAndFechaAccionBetween(negocioId, fechaInicio, fechaFin).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegistroAuditoriaResponse obtenerRegistroPorId(Long id) {
        RegistroAuditoria registro = registroAuditoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de auditoría no encontrado con ID: " + id));
        return convertirAResponse(registro);
    }

    @Transactional
    public RegistroAuditoriaResponse crearRegistro(RegistroAuditoriaRequest request) {
        RegistroAuditoria registro = new RegistroAuditoria();
        mapearRequestAEntidad(request, registro);
        RegistroAuditoria guardado = registroAuditoriaRepository.save(registro);
        return convertirAResponse(guardado);
    }

    @Transactional
    public RegistroAuditoriaResponse actualizarRegistro(Long id, RegistroAuditoriaRequest request) {
        RegistroAuditoria registro = registroAuditoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de auditoría no encontrado con ID: " + id));

        mapearRequestAEntidad(request, registro);
        RegistroAuditoria actualizado = registroAuditoriaRepository.save(registro);
        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminarRegistro(Long id) {
        if (!registroAuditoriaRepository.existsById(id)) {
            throw new RuntimeException("Registro de auditoría no encontrado con ID: " + id);
        }
        registroAuditoriaRepository.deleteById(id);
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(RegistroAuditoriaRequest request, RegistroAuditoria registro) {
        registro.setNegocioId(request.getNegocioId());
        registro.setUsuarioId(request.getUsuarioId());
        registro.setAccion(request.getAccion());
        registro.setTipoEntidad(request.getTipoEntidad());
        registro.setEntidadId(request.getEntidadId());
        registro.setValoresAnteriores(request.getValoresAnteriores());
        registro.setValoresNuevos(request.getValoresNuevos());
        registro.setIpAddress(request.getIpAddress());
        registro.setUserAgent(request.getUserAgent());
    }

    private RegistroAuditoriaResponse convertirAResponse(RegistroAuditoria registro) {
        RegistroAuditoriaResponse response = new RegistroAuditoriaResponse();
        response.setId(registro.getId());
        response.setNegocioId(registro.getNegocioId());
        response.setUsuarioId(registro.getUsuarioId());
        response.setAccion(registro.getAccion());
        response.setTipoEntidad(registro.getTipoEntidad());
        response.setEntidadId(registro.getEntidadId());
        response.setValoresAnteriores(registro.getValoresAnteriores());
        response.setValoresNuevos(registro.getValoresNuevos());
        response.setIpAddress(registro.getIpAddress());
        response.setUserAgent(registro.getUserAgent());
        response.setFechaAccion(registro.getFechaAccion());
        return response;
    }
}
