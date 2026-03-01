package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.MetodosPago;

public interface MetodosPagoRepository extends JpaRepository<MetodosPago, Long> {

    List<MetodosPago> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    List<MetodosPago> findByNegocioIdAndDisponiblePos(Long negocioId, Boolean disponiblePos);

    List<MetodosPago> findByNegocioId(Long negocioId);
}
