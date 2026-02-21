package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.MovimientoInventario;
import DrinkGo.DrinkGo_backend.entity.MovimientoInventario.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByNegocioId(Long negocioId);

    Optional<MovimientoInventario> findByIdAndNegocioId(Long id, Long negocioId);

    List<MovimientoInventario> findByProductoIdAndAlmacenIdAndNegocioId(Long productoId, Long almacenId, Long negocioId);

    List<MovimientoInventario> findByTipoMovimientoAndNegocioId(TipoMovimiento tipoMovimiento, Long negocioId);

    List<MovimientoInventario> findByLoteIdAndNegocioId(Long loteId, Long negocioId);

    List<MovimientoInventario> findByNegocioIdAndCreadoEnBetween(Long negocioId,
                                                                  LocalDateTime desde,
                                                                  LocalDateTime hasta);

    List<MovimientoInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);
}
