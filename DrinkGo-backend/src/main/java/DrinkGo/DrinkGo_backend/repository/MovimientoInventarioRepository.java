package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para movimientos de inventario (RF-INV-004..006).
 * Todas las consultas filtran por negocio_id (multi-tenant obligatorio).
 */
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    /** Listar todos los movimientos de un negocio, ordenados por fecha descendente */
    List<MovimientoInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    /** Buscar movimiento por ID y negocio */
    Optional<MovimientoInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar movimientos de un producto */
    List<MovimientoInventario> findByProductoIdAndNegocioIdOrderByCreadoEnDesc(Long productoId, Long negocioId);

    /** Listar movimientos de un almacén */
    List<MovimientoInventario> findByAlmacenIdAndNegocioIdOrderByCreadoEnDesc(Long almacenId, Long negocioId);

    /** Listar movimientos de un lote */
    List<MovimientoInventario> findByLoteIdAndNegocioIdOrderByCreadoEnDesc(Long loteId, Long negocioId);
}
