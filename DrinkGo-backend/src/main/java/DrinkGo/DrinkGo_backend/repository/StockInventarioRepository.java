package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import DrinkGo.DrinkGo_backend.entity.Almacenes;
import DrinkGo.DrinkGo_backend.entity.Productos;
import DrinkGo.DrinkGo_backend.entity.StockInventario;

public interface StockInventarioRepository extends JpaRepository<StockInventario, Long> {

    List<StockInventario> findByNegocioId(Long negocioId);

    Optional<StockInventario> findByProductoIdAndAlmacenId(Long productoId, Long almacenId);

    List<StockInventario> findByProductoId(Long productoId);

    @Query("SELECT s FROM StockInventario s WHERE s.producto.id = :productoId AND s.negocio.id = :negocioId")
    List<StockInventario> findByProductoIdAndNegocioId(
            @Param("productoId") Long productoId,
            @Param("negocioId") Long negocioId);

    @Query(value = "SELECT * FROM stock_inventario WHERE producto_id = :productoId AND almacen_id = :almacenId FOR UPDATE", nativeQuery = true)
    Optional<StockInventario> findByProductoIdAndAlmacenIdForUpdate(
            @Param("productoId") Long productoId,
            @Param("almacenId") Long almacenId);

    /**
     * Busca el registro de stock para un producto específico en un almacén específico
     */
    @Query("SELECT s FROM StockInventario s WHERE s.producto = :producto AND s.almacen = :almacen AND s.estaActivo = true")
    Optional<StockInventario> findByProductoAndAlmacen(@Param("producto") Productos producto, @Param("almacen") Almacenes almacen);

    /**
     * Busca todos los stocks de un producto en todos los almacenes
     */
    @Query("SELECT s FROM StockInventario s WHERE s.producto = :producto AND s.estaActivo = true")
    List<StockInventario> findByProducto(@Param("producto") Productos producto);

    /**
     * Busca productos con stock bajo (cantidad disponible menor al mínimo)
     */
    @Query("SELECT s FROM StockInventario s WHERE s.cantidadDisponible < :cantidadMinima AND s.estaActivo = true")
    List<StockInventario> findStockBajo(@Param("cantidadMinima") java.math.BigDecimal cantidadMinima);

    /**
     * Busca stocks de un almacén específico
     */
    @Query("SELECT s FROM StockInventario s WHERE s.almacen = :almacen AND s.estaActivo = true")
    List<StockInventario> findByAlmacen(@Param("almacen") Almacenes almacen);
}
