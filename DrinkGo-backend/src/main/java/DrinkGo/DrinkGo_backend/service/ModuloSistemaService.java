package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.ModuloSistemaRequest;
import DrinkGo.DrinkGo_backend.dto.ModuloSistemaResponse;
import DrinkGo.DrinkGo_backend.entity.ModuloSistema;
import DrinkGo.DrinkGo_backend.repository.ModuloSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para ModuloSistema
 */
@Service
public class ModuloSistemaService {

    @Autowired
    private ModuloSistemaRepository moduloSistemaRepository;

    @Transactional(readOnly = true)
    public List<ModuloSistemaResponse> obtenerTodosLosModulos() {
        return moduloSistemaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ModuloSistemaResponse> obtenerModulosActivos() {
        return moduloSistemaRepository.findByEstaActivoTrueOrderByOrdenAsc().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ModuloSistemaResponse> obtenerModulosPrincipales() {
        return moduloSistemaRepository.findByModuloPadreIdIsNull().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ModuloSistemaResponse> obtenerSubmodulos(Long moduloPadreId) {
        return moduloSistemaRepository.findByModuloPadreId(moduloPadreId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ModuloSistemaResponse obtenerModuloPorId(Long id) {
        ModuloSistema modulo = moduloSistemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado con ID: " + id));
        return convertirAResponse(modulo);
    }

    @Transactional(readOnly = true)
    public ModuloSistemaResponse obtenerModuloPorCodigo(String codigo) {
        ModuloSistema modulo = moduloSistemaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado con código: " + codigo));
        return convertirAResponse(modulo);
    }

    @Transactional
    public ModuloSistemaResponse crearModulo(ModuloSistemaRequest request) {
        if (moduloSistemaRepository.existsByCodigo(request.getCodigo())) {
            throw new RuntimeException("Ya existe un módulo con el código: " + request.getCodigo());
        }

        ModuloSistema modulo = new ModuloSistema();
        mapearRequestAEntidad(request, modulo);
        ModuloSistema guardado = moduloSistemaRepository.save(modulo);
        return convertirAResponse(guardado);
    }

    @Transactional
    public ModuloSistemaResponse actualizarModulo(Long id, ModuloSistemaRequest request) {
        ModuloSistema modulo = moduloSistemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado con ID: " + id));

        if (!modulo.getCodigo().equals(request.getCodigo()) 
            && moduloSistemaRepository.existsByCodigo(request.getCodigo())) {
            throw new RuntimeException("Ya existe un módulo con el código: " + request.getCodigo());
        }

        mapearRequestAEntidad(request, modulo);
        ModuloSistema actualizado = moduloSistemaRepository.save(modulo);
        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminarModulo(Long id) {
        if (!moduloSistemaRepository.existsById(id)) {
            throw new RuntimeException("Módulo no encontrado con ID: " + id);
        }
        moduloSistemaRepository.deleteById(id);
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(ModuloSistemaRequest request, ModuloSistema modulo) {
        modulo.setCodigo(request.getCodigo());
        modulo.setNombre(request.getNombre());
        modulo.setDescripcion(request.getDescripcion());
        modulo.setModuloPadreId(request.getModuloPadreId());
        modulo.setIcono(request.getIcono());
        modulo.setOrden(request.getOrden() != null ? request.getOrden() : 0);
        if (request.getEstaActivo() != null) {
            modulo.setEstaActivo(request.getEstaActivo());
        }
    }

    private ModuloSistemaResponse convertirAResponse(ModuloSistema modulo) {
        ModuloSistemaResponse response = new ModuloSistemaResponse();
        response.setId(modulo.getId());
        response.setCodigo(modulo.getCodigo());
        response.setNombre(modulo.getNombre());
        response.setDescripcion(modulo.getDescripcion());
        response.setModuloPadreId(modulo.getModuloPadreId());
        response.setIcono(modulo.getIcono());
        response.setOrden(modulo.getOrden());
        response.setEstaActivo(modulo.getEstaActivo());
        return response;
    }
}
