package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.StockInventario;
import DrinkGo.DrinkGo_backend.entity.Productos;
import DrinkGo.DrinkGo_backend.entity.Almacenes;
import java.util.Optional;
import java.util.List;

public interface StockInventarioRepository extends JpaRepository<StockInventario, Long> {

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
