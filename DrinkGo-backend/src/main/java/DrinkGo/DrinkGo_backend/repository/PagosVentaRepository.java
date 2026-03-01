package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.PagosVenta;

public interface PagosVentaRepository extends JpaRepository<PagosVenta, Long> {

    List<PagosVenta> findByVentaId(Long ventaId);
}
