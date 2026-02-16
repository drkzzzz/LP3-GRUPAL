package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para movimientos de inventario (RF-INV-004..006).
 * Todas las consultas filtran por negocio_id para garantizar el aislamiento de datos.
 */
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    /** Buscar movimiento por ID y negocio (Seguridad multi-tenant) */
    Optional<MovimientoInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar el historial completo de un negocio */
    List<MovimientoInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    /** Kardex por producto: historial de movimientos de un producto específico */
    List<MovimientoInventario> findByNegocioIdAndProductoIdOrderByCreadoEnDesc(
            Long negocioId, Long productoId);

    /** Historial de un almacén específico */
    List<MovimientoInventario> findByNegocioIdAndAlmacenIdOrderByCreadoEnDesc(
            Long negocioId, Long almacenId);

    /** Historial detallado: producto específico en un almacén específico */
    List<MovimientoInventario> findByNegocioIdAndProductoIdAndAlmacenIdOrderByCreadoEnDesc(
            Long negocioId, Long productoId, Long almacenId);

    /** Filtrar por tipo (ej: solo 'salida_venta' o solo 'merma') */
    List<MovimientoInventario> findByNegocioIdAndTipoMovimientoOrderByCreadoEnDesc(
            Long negocioId, MovimientoInventario.TipoMovimiento tipoMovimiento);

    /** Historial de un lote específico (Trazabilidad de lote) */
    List<MovimientoInventario> findByLoteIdAndNegocioIdOrderByCreadoEnDesc(
            Long loteId, Long negocioId);
}