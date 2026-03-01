package DrinkGo.DrinkGo_backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.MovimientosCaja;

public interface MovimientosCajaRepository extends JpaRepository<MovimientosCaja, Long> {

    List<MovimientosCaja> findBySesionCajaIdOrderByFechaMovimientoDesc(Long sesionCajaId);

    @Query("SELECT m FROM MovimientosCaja m " +
           "JOIN FETCH m.sesionCaja s " +
           "JOIN FETCH s.caja c " +
           "JOIN FETCH s.usuario " +
           "LEFT JOIN FETCH m.categoriaGasto " +
           "WHERE c.negocio.id = :negocioId " +
           "AND m.fechaMovimiento BETWEEN :desde AND :hasta " +
           "ORDER BY m.fechaMovimiento DESC")
    List<MovimientosCaja> findByNegocioAndDateRange(
        @Param("negocioId") Long negocioId,
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta);

    @Query("SELECT m FROM MovimientosCaja m " +
           "JOIN FETCH m.sesionCaja s " +
           "JOIN FETCH s.caja c " +
           "JOIN FETCH s.usuario " +
           "LEFT JOIN FETCH m.categoriaGasto " +
           "WHERE c.negocio.id = :negocioId " +
           "AND c.id = :cajaId " +
           "AND m.fechaMovimiento BETWEEN :desde AND :hasta " +
           "ORDER BY m.fechaMovimiento DESC")
    List<MovimientosCaja> findByNegocioAndCajaAndDateRange(
        @Param("negocioId") Long negocioId,
        @Param("cajaId") Long cajaId,
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta);
}
