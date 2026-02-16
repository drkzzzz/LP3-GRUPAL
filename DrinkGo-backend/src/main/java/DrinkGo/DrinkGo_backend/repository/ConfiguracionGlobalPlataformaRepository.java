package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.ConfiguracionGlobalPlataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de configuraciones globales de plataforma.
 */
@Repository
public interface ConfiguracionGlobalPlataformaRepository extends JpaRepository<ConfiguracionGlobalPlataforma, Long> {

    Optional<ConfiguracionGlobalPlataforma> findByClaveConfiguracion(String claveConfiguracion);

    boolean existsByClaveConfiguracion(String claveConfiguracion);
}
