package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.CategoriasGasto;

public interface CategoriasGastoRepository extends JpaRepository<CategoriasGasto, Long> {
    java.util.List<CategoriasGasto> findByNegocioId(Long negocioId);
}
