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

    Optional<StockInventario> findByIdAndNegocioId(Long id, Long negocioId);

    Optional<StockInventario> findByProductoIdAndAlmacenIdAndNegocioId(Long productoId, Long almacenId, Long negocioId);

    List<StockInventario> findByAlmacenIdAndNegocioId(Long almacenId, Long negocioId);

    List<StockInventario> findByProductoIdAndNegocioId(Long productoId, Long negocioId);

    /**
     * Stock bajo: registros cuya cantidad_total está por debajo de un umbral.
     */
    @Query(value = "SELECT * FROM stock_inventario " +
            "WHERE negocio_id = :negocioId AND cantidad_total <= :umbral " +
            "ORDER BY cantidad_total ASC",
            nativeQuery = true)
    List<StockInventario> findStockBajo(@Param("negocioId") Long negocioId,
                                        @Param("umbral") int umbral);
}
