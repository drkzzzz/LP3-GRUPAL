package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.ConfiguracionNegocio;
import DrinkGo.DrinkGo_backend.repository.ConfiguracionNegocioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConfiguracionNegocioService {

    private final ConfiguracionNegocioRepository configuracionRepository;

    public ConfiguracionNegocioService(ConfiguracionNegocioRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionNegocio> findByNegocio(Long negocioId) {
        return configuracionRepository.findByNegocioId(negocioId);
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionNegocio> findByNegocioYCategoria(Long negocioId, String categoria) {
        return configuracionRepository.findByNegocioIdAndCategoria(negocioId, categoria);
    }

    @Transactional(readOnly = true)
    public Optional<ConfiguracionNegocio> findByClave(Long negocioId, String clave) {
        return configuracionRepository.findByNegocioIdAndClaveConfiguracion(negocioId, clave);
    }

    @Transactional(readOnly = true)
    public ConfiguracionNegocio findById(Long id) {
        return configuracionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));
    }

    @Transactional
    public ConfiguracionNegocio crear(ConfiguracionNegocio configuracion) {
        return configuracionRepository.save(configuracion);
    }

    @Transactional
    public ConfiguracionNegocio actualizar(Long id, ConfiguracionNegocio configuracion) {
        ConfiguracionNegocio existente = findById(id);
        existente.setValorConfiguracion(configuracion.getValorConfiguracion());
        existente.setTipoValor(configuracion.getTipoValor());
        existente.setCategoria(configuracion.getCategoria());
        existente.setDescripcion(configuracion.getDescripcion());
        return configuracionRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        configuracionRepository.deleteById(id);
    }

    @Transactional
    public ConfiguracionNegocio guardarOActualizar(Long negocioId, String clave, String valor) {
        Optional<ConfiguracionNegocio> existente = findByClave(negocioId, clave);

        if (existente.isPresent()) {
            ConfiguracionNegocio config = existente.get();
            config.setValorConfiguracion(valor);
            return configuracionRepository.save(config);
        } else {
            ConfiguracionNegocio nuevaConfig = new ConfiguracionNegocio();
            nuevaConfig.setNegocioId(negocioId);
            nuevaConfig.setClaveConfiguracion(clave);
            nuevaConfig.setValorConfiguracion(valor);
            return configuracionRepository.save(nuevaConfig);
        }
    }
}
