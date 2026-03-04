package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.PagosVenta;

public interface PagosVentaRepository extends JpaRepository<PagosVenta, Long> {

    @Query("SELECT p FROM PagosVenta p WHERE p.venta.id = :ventaId")
    List<PagosVenta> findByVentaId(@Param("ventaId") Long ventaId);
}
