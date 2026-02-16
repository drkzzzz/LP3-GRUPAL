package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.ConfiguracionRequest;
import DrinkGo.DrinkGo_backend.entity.ConfiguracionGlobalPlataforma;
import DrinkGo.DrinkGo_backend.repository.ConfiguracionGlobalPlataformaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de configuración global de plataforma - Bloque 1.
 * Implementa RF-CGL-001.
 */
@Service
public class ConfiguracionGlobalService {

    @Autowired
    private ConfiguracionGlobalPlataformaRepository configuracionRepository;

    /**
     * Crear nueva configuración global (RF-CGL-001).
     */
    @Transactional
    public ConfiguracionGlobalPlataforma crearConfiguracion(ConfiguracionRequest request) {
        // Validaciones
        if (request.getClaveConfiguracion() == null || request.getClaveConfiguracion().isBlank()) {
            throw new RuntimeException("La clave de configuración es obligatoria");
        }
        if (request.getValor() == null) {
            throw new RuntimeException("El valor es obligatorio");
        }
        if (request.getTipoDato() == null || request.getTipoDato().isBlank()) {
            throw new RuntimeException("El tipo de dato es obligatorio");
        }

        // Verificar que no exista
        if (configuracionRepository.existsByClaveConfiguracion(request.getClaveConfiguracion())) {
            throw new RuntimeException("Ya existe una configuración con la clave: " + request.getClaveConfiguracion());
        }

        ConfiguracionGlobalPlataforma config = new ConfiguracionGlobalPlataforma();
        config.setClaveConfiguracion(request.getClaveConfiguracion());
        config.setValor(request.getValor());
        config.setValorConfiguracion(request.getValor()); // Duplicar para compatibilidad con tabla
        config.setTipoDato(request.getTipoDato());
        config.setTipoValor(request.getTipoDato()); // Duplicar para compatibilidad
        config.setDescripcion(request.getDescripcion());
        config.setEsPublica(request.getEsPublica() != null ? request.getEsPublica() : false);

        return configuracionRepository.save(config);
    }

    /**
     * Listar todas las configuraciones.
     */
    public List<ConfiguracionGlobalPlataforma> listarConfiguraciones() {
        return configuracionRepository.findAll();
    }

    /**
     * Obtener configuración por clave.
     */
    public ConfiguracionGlobalPlataforma obtenerPorClave(String clave) {
        return configuracionRepository.findByClaveConfiguracion(clave)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada: " + clave));
    }

    /**
     * Obtener configuración por ID.
     */
    public ConfiguracionGlobalPlataforma obtenerPorId(Long id) {
        return configuracionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada con ID: " + id));
    }

    /**
     * Actualizar configuración existente.
     */
    @Transactional
    public ConfiguracionGlobalPlataforma actualizarConfiguracion(Long id, ConfiguracionRequest request) {
        ConfiguracionGlobalPlataforma config = obtenerPorId(id);

        if (request.getValor() != null) {
            config.setValor(request.getValor());
            config.setValorConfiguracion(request.getValor()); // Duplicar para compatibilidad
        }
        if (request.getTipoDato() != null) {
            config.setTipoDato(request.getTipoDato());
            config.setTipoValor(request.getTipoDato()); // Duplicar para compatibilidad
        }
        if (request.getDescripcion() != null) {
            config.setDescripcion(request.getDescripcion());
        }
        if (request.getEsPublica() != null) {
            config.setEsPublica(request.getEsPublica());
        }

        return configuracionRepository.save(config);
    }

    /**
     * Eliminar configuración.
     */
    @Transactional
    public void eliminarConfiguracion(Long id) {
        ConfiguracionGlobalPlataforma config = obtenerPorId(id);
        configuracionRepository.delete(config);
    }
}
