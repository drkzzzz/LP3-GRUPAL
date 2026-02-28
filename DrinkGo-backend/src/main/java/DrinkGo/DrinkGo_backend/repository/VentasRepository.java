package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.Ventas;

import java.util.List;
import java.util.Optional;

public interface VentasRepository extends JpaRepository<Ventas, Long> {

    List<Ventas> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    List<Ventas> findBySesionCajaIdOrderByCreadoEnDesc(Long sesionCajaId);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(v.numeroVenta, 5) AS int)), 0) FROM Ventas v WHERE v.negocio.id = :negocioId")
    int findMaxNumeroVentaByNegocioId(@Param("negocioId") Long negocioId);

    Optional<Ventas> findByIdAndNegocioId(Long id, Long negocioId);

    List<Ventas> findBySesionCajaIdAndEstado(Long sesionCajaId, Ventas.Estado estado);
}
