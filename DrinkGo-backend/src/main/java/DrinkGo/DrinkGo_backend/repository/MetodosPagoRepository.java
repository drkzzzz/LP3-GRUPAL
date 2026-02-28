package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.MetodosPago;

import java.util.List;

public interface MetodosPagoRepository extends JpaRepository<MetodosPago, Long> {

    List<MetodosPago> findByNegocioIdAndDisponiblePosTrue(Long negocioId);

    List<MetodosPago> findByNegocioId(Long negocioId);
}
