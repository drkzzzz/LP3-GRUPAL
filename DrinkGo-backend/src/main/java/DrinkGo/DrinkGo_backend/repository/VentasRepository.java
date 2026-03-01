package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.Ventas;

public interface VentasRepository extends JpaRepository<Ventas, Long> {

    List<Ventas> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    List<Ventas> findBySesionCajaIdOrderByCreadoEnDesc(Long sesionCajaId);

    @Query("SELECT COUNT(v) FROM Ventas v WHERE v.negocio.id = :negocioId")
    long countByNegocioId(@Param("negocioId") Long negocioId);
}
