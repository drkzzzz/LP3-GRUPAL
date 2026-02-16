package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.CategoriaGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para categorías de gasto.
 */
@Repository
public interface CategoriaGastoRepository extends JpaRepository<CategoriaGasto, Long> {

    List<CategoriaGasto> findByNegocioIdAndEstaActivoTrue(Long negocioId);

    List<CategoriaGasto> findByNegocioId(Long negocioId);
}
