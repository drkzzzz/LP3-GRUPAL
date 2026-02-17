package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.ConfiguracionNegocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionNegocioRepository extends JpaRepository<ConfiguracionNegocio, Long> {
    List<ConfiguracionNegocio> findByNegocioId(Long negocioId);

    Optional<ConfiguracionNegocio> findByNegocioIdAndClaveConfiguracion(Long negocioId, String claveConfiguracion);

    List<ConfiguracionNegocio> findByNegocioIdAndCategoria(Long negocioId, String categoria);
}
