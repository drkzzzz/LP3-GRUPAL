package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.MovimientosInventario;

public interface MovimientosInventarioRepository extends JpaRepository<MovimientosInventario, Long> {

    List<MovimientosInventario> findByNegocioId(Long negocioId);

}
