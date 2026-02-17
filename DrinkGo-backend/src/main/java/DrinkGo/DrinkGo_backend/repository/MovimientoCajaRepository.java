package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.MovimientoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para movimientos de caja.
 * Bloque 8 - Ventas, POS y Cajas.
 */
@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {

    /** Buscar movimiento por ID y negocio (Seguridad multi-tenant) */
    Optional<MovimientoCaja> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar todos los movimientos de un negocio */
    List<MovimientoCaja> findByNegocioId(Long negocioId);

    /** Listar movimientos de una sesión específica */
    List<MovimientoCaja> findByNegocioIdAndSesionId(Long negocioId, Long sesionId);

    /** Listar movimientos por tipo */
    List<MovimientoCaja> findByNegocioIdAndTipoMovimiento(Long negocioId, String tipoMovimiento);

    /** Listar movimientos de una sesión por tipo */
    List<MovimientoCaja> findByNegocioIdAndSesionIdAndTipoMovimiento(Long negocioId, Long sesionId,
            String tipoMovimiento);

    /** Calcular total de movimientos por tipo en una sesión */
    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m WHERE m.negocioId = :negocioId AND m.sesionId = :sesionId AND m.tipoMovimiento = :tipoMovimiento")
    BigDecimal calcularTotalPorTipo(
            @Param("negocioId") Long negocioId,
            @Param("sesionId") Long sesionId,
            @Param("tipoMovimiento") String tipoMovimiento);
}
