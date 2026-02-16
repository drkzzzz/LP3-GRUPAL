package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    Optional<MovimientoInventario> findByIdAndNegocioId(Long id, Long negocioId);

    List<MovimientoInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    List<MovimientoInventario> findByNegocioIdAndProductoIdOrderByCreadoEnDesc(
            Long negocioId, Long productoId);

    List<MovimientoInventario> findByNegocioIdAndAlmacenIdOrderByCreadoEnDesc(
            Long negocioId, Long almacenId);

    List<MovimientoInventario> findByNegocioIdAndProductoIdAndAlmacenIdOrderByCreadoEnDesc(
            Long negocioId, Long productoId, Long almacenId);

    List<MovimientoInventario> findByNegocioIdAndTipoMovimientoOrderByCreadoEnDesc(
            Long negocioId, MovimientoInventario.TipoMovimiento tipoMovimiento);
}
