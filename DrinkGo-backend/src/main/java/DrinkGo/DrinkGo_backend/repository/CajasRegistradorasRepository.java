package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.CajasRegistradoras;

import java.util.List;
import java.util.Optional;

public interface CajasRegistradorasRepository extends JpaRepository<CajasRegistradoras, Long> {

    List<CajasRegistradoras> findByNegocioId(Long negocioId);

    Optional<CajasRegistradoras> findByIdAndNegocioId(Long id, Long negocioId);
}
