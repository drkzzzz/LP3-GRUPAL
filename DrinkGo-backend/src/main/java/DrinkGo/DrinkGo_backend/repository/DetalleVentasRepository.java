package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.DetalleVentas;

public interface DetalleVentasRepository extends JpaRepository<DetalleVentas, Long> {

    @Query("SELECT d FROM DetalleVentas d WHERE d.venta.id = :ventaId")
    List<DetalleVentas> findByVentaId(@Param("ventaId") Long ventaId);

    @Query("SELECT d FROM DetalleVentas d JOIN FETCH d.venta v WHERE v.negocio.id = :negocioId ORDER BY v.creadoEn DESC")
    List<DetalleVentas> findByVentaNegocioId(@Param("negocioId") Long negocioId);
}
