package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DrinkGo.DrinkGo_backend.entity.ConfiguracionPse;

@Repository
public interface ConfiguracionPseRepository extends JpaRepository<ConfiguracionPse, Long> {

    Optional<ConfiguracionPse> findFirstByNegocioId(Long negocioId);

    List<ConfiguracionPse> findAll();
}
