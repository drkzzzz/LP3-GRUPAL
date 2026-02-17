package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.PermisoSistemaRequest;
import DrinkGo.DrinkGo_backend.dto.PermisoSistemaResponse;
import DrinkGo.DrinkGo_backend.entity.PermisoSistema;
import DrinkGo.DrinkGo_backend.repository.PermisoSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para PermisoSistema
 */
@Service
public class PermisoSistemaService {

    @Autowired
    private PermisoSistemaRepository permisoSistemaRepository;

    @Transactional(readOnly = true)
    public List<PermisoSistemaResponse> obtenerTodosLosPermisos() {
        return permisoSistemaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PermisoSistemaResponse> obtenerPermisosPorModulo(Long moduloId) {
        return permisoSistemaRepository.findByModuloId(moduloId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PermisoSistemaResponse obtenerPermisoPorId(Long id) {
        PermisoSistema permiso = permisoSistemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con ID: " + id));
        return convertirAResponse(permiso);
    }

    @Transactional(readOnly = true)
    public PermisoSistemaResponse obtenerPermisoPorCodigo(String codigo) {
        PermisoSistema permiso = permisoSistemaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con código: " + codigo));
        return convertirAResponse(permiso);
    }

    @Transactional
    public PermisoSistemaResponse crearPermiso(PermisoSistemaRequest request) {
        if (permisoSistemaRepository.existsByCodigo(request.getCodigo())) {
            throw new RuntimeException("Ya existe un permiso con el código: " + request.getCodigo());
        }

        PermisoSistema permiso = new PermisoSistema();
        mapearRequestAEntidad(request, permiso);
        PermisoSistema guardado = permisoSistemaRepository.save(permiso);
        return convertirAResponse(guardado);
    }

    @Transactional
    public PermisoSistemaResponse actualizarPermiso(Long id, PermisoSistemaRequest request) {
        PermisoSistema permiso = permisoSistemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con ID: " + id));

        if (!permiso.getCodigo().equals(request.getCodigo()) 
            && permisoSistemaRepository.existsByCodigo(request.getCodigo())) {
            throw new RuntimeException("Ya existe un permiso con el código: " + request.getCodigo());
        }

        mapearRequestAEntidad(request, permiso);
        PermisoSistema actualizado = permisoSistemaRepository.save(permiso);
        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminarPermiso(Long id) {
        if (!permisoSistemaRepository.existsById(id)) {
            throw new RuntimeException("Permiso no encontrado con ID: " + id);
        }
        permisoSistemaRepository.deleteById(id);
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(PermisoSistemaRequest request, PermisoSistema permiso) {
        permiso.setModuloId(request.getModuloId());
        permiso.setCodigo(request.getCodigo());
        permiso.setNombre(request.getNombre());
        permiso.setDescripcion(request.getDescripcion());
        
        if (request.getTipoAccion() != null) {
            permiso.setTipoAccion(PermisoSistema.TipoAccion.valueOf(request.getTipoAccion()));
        }
    }

    private PermisoSistemaResponse convertirAResponse(PermisoSistema permiso) {
        PermisoSistemaResponse response = new PermisoSistemaResponse();
        response.setId(permiso.getId());
        response.setModuloId(permiso.getModuloId());
        response.setCodigo(permiso.getCodigo());
        response.setNombre(permiso.getNombre());
        response.setDescripcion(permiso.getDescripcion());
        response.setTipoAccion(permiso.getTipoAccion() != null ? permiso.getTipoAccion().name() : null);
        return response;
    }
}
