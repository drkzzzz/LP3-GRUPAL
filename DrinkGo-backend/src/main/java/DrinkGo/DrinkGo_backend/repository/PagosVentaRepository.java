package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.PagosVenta;

import java.util.List;

public interface PagosVentaRepository extends JpaRepository<PagosVenta, Long> {

    List<PagosVenta> findByVentaId(Long ventaId);
}
