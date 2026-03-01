package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.Sedes;

public interface SedesRepository extends JpaRepository<Sedes, Long> {
    java.util.List<Sedes> findByNegocioId(Long negocioId);
}
