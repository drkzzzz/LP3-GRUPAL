package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import DrinkGo.DrinkGo_backend.entity.LotesInventario;

public interface LotesInventarioRepository extends JpaRepository<LotesInventario, Long> {

    /** Lotes con stock disponible, ordenados FIFO (más antiguo primero) */
    @Query("SELECT l FROM LotesInventario l WHERE l.producto.id = :productoId "
            + "AND l.almacen.id = :almacenId AND l.cantidadActual > 0 "
            + "ORDER BY l.fechaIngreso ASC, l.id ASC")
    List<LotesInventario> findLotesConStockFIFO(
            @Param("productoId") Long productoId,
            @Param("almacenId") Long almacenId);

    @Query(value = "SELECT * FROM lotes_inventario WHERE producto_id = :productoId "
            + "AND almacen_id = :almacenId AND cantidad_actual > 0 "
            + "ORDER BY fecha_ingreso ASC, id ASC FOR UPDATE", nativeQuery = true)
    List<LotesInventario> findLotesConStockFIFOForUpdate(
            @Param("productoId") Long productoId,
            @Param("almacenId") Long almacenId);
}
