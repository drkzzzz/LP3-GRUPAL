package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.StockInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockInventarioRepository extends JpaRepository<StockInventario, Long> {

    List<StockInventario> findByNegocioId(Long negocioId);

    List<StockInventario> findByNegocioIdAndProductoId(Long negocioId, Long productoId);

    Optional<StockInventario> findByNegocioIdAndProductoIdAndAlmacenId(
            Long negocioId, Long productoId, Long almacenId);

    List<StockInventario> findByNegocioIdAndAlmacenId(Long negocioId, Long almacenId);

    // Stock bajo: cantidad_en_mano <= stock_minimo del producto
    @Query(value = "SELECT si.* FROM stock_inventario si " +
            "INNER JOIN productos p ON si.producto_id = p.id " +
            "WHERE si.negocio_id = :negocioId " +
            "AND si.cantidad_en_mano <= p.stock_minimo " +
            "AND p.esta_activo = 1",
            nativeQuery = true)
    List<StockInventario> findStockBajo(@Param("negocioId") Long negocioId);
}
