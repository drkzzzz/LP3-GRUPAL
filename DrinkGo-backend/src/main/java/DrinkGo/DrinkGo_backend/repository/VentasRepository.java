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

    @Query("SELECT COUNT(v) FROM Ventas v WHERE v.sede.id = :sedeId")
    long countBySedeId(@Param("sedeId") Long sedeId);

    @Query(value = "SELECT MAX(CAST(SUBSTRING(v.numero_venta, LENGTH(:prefix) + 1) AS UNSIGNED)) FROM ventas v WHERE v.sede_id = :sedeId AND v.numero_venta LIKE CONCAT(:prefix, '%')", nativeQuery = true)
    Integer findMaxSequenceBySedAndPrefix(@Param("sedeId") Long sedeId, @Param("prefix") String prefix);
}
