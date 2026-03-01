package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.DetalleVentas;

public interface DetalleVentasRepository extends JpaRepository<DetalleVentas, Long> {

    List<DetalleVentas> findByVentaId(Long ventaId);
}
