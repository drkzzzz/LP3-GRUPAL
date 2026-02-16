package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.StockInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para stock de inventario (RF-INV-001).
 * Gestiona las existencias actuales filtrando siempre por negocio_id.
 */
@Repository
public interface StockInventarioRepository extends JpaRepository<StockInventario, Long> {

    /** Buscar stock por ID y negocio (Seguridad multi-tenant) */
    Optional<StockInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar todo el stock de un negocio */
    List<StockInventario> findByNegocioId(Long negocioId);

    /** Listar stock de un producto en todos los almacenes del negocio */
    List<StockInventario> findByNegocioIdAndProductoId(Long negocioId, Long productoId);

    /** Buscar el registro de stock específico de un producto en un almacén determinado */
    Optional<StockInventario> findByNegocioIdAndProductoIdAndAlmacenId(
            Long negocioId, Long productoId, Long almacenId);

    /** Listar existencias de un almacén específico */
    List<StockInventario> findByNegocioIdAndAlmacenId(Long negocioId, Long almacenId);

    /** * Consulta para detectar stock bajo comparando con el stock_minimo definido en Producto.
     * Se usa Native Query para facilitar el JOIN con la tabla de productos de MySQL.
     */
    @Query(value = "SELECT si.* FROM stock_inventario si " +
            "INNER JOIN productos p ON si.producto_id = p.id " +
            "WHERE si.negocio_id = :negocioId " +
            "AND si.cantidad_en_mano <= p.stock_minimo " +
            "AND p.esta_activo = 1", 
            nativeQuery = true)
    List<StockInventario> findStockBajo(@Param("negocioId") Long negocioId);
}