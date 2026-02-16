package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para gastos.
 */
@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByNegocioId(Long negocioId);

    List<Gasto> findByNegocioIdAndSedeId(Long negocioId, Long sedeId);

    Optional<Gasto> findByIdAndNegocioId(Long id, Long negocioId);

    List<Gasto> findByNegocioIdAndEstado(Long negocioId, Gasto.EstadoGasto estado);

    List<Gasto> findByNegocioIdAndCategoriaId(Long negocioId, Long categoriaId);

    List<Gasto> findByNegocioIdAndFechaGastoBetween(Long negocioId, LocalDate fechaInicio, LocalDate fechaFin);

    @Query("SELECT g FROM Gasto g WHERE g.negocioId = :negocioId AND g.sedeId = :sedeId AND g.fechaGasto BETWEEN :fechaInicio AND :fechaFin ORDER BY g.fechaGasto DESC")
    List<Gasto> findGastosPorSedeYFecha(
        @Param("negocioId") Long negocioId,
        @Param("sedeId") Long sedeId,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT SUM(g.total) FROM Gasto g WHERE g.negocioId = :negocioId AND g.estado <> 'anulado' AND g.fechaGasto BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularTotalGastosPorPeriodo(
        @Param("negocioId") Long negocioId,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );
}
