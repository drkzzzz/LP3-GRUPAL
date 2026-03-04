package DrinkGo.DrinkGo_backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import DrinkGo.DrinkGo_backend.entity.ConfiguracionPse;
import DrinkGo.DrinkGo_backend.repository.ConfiguracionPseRepository;

/**
 * Factory que resuelve el PseProvider adecuado según la
 * configuración del negocio.
 * <p>
 * En esta versión usa siempre el {@link PseSimuladorService}
 * como proveedor. Cuando se integren PSE reales, este factory
 * retornará la implementación correspondiente (Nubefact, Efact, etc.)
 * según lo configurado en {@code configuracion_pse}.
 */
@Service
public class PseProviderFactory {

    @Autowired
    private PseSimuladorService pseSimuladorService;

    @Autowired
    private ConfiguracionPseRepository configPseRepo;

    /**
     * Retorna el proveedor PSE configurado para el negocio.
     * Por defecto retorna el simulador.
     */
    public PseProvider getProvider(Long negocioId) {
        // En el futuro, aquí se resolverá el proveedor real
        // según la configuración del negocio.
        // Optional<ConfiguracionPse> config = configPseRepo.findByNegocioId(negocioId);
        // if (config.isPresent()) { ... switch por proveedor ... }
        return pseSimuladorService;
    }

    /**
     * Retorna el nombre del proveedor PSE actual para un negocio.
     */
    public String getProveedorActual(Long negocioId) {
        Optional<ConfiguracionPse> config = configPseRepo.findFirstByNegocioId(negocioId);
        return config.map(ConfiguracionPse::getProveedor).orElse("SIMULADOR");
    }
}
