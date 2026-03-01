package DrinkGo.DrinkGo_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import DrinkGo.DrinkGo_backend.entity.Almacenes;

public interface AlmacenesRepository extends JpaRepository<Almacenes, Long> {

    Optional<Almacenes> findByNegocioIdAndEsPredeterminado(Long negocioId, Boolean esPredeterminado);
}
