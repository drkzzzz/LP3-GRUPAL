package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.DetalleVentas;

import java.util.List;

public interface DetalleVentasRepository extends JpaRepository<DetalleVentas, Long> {

    List<DetalleVentas> findByVentaId(Long ventaId);
}
