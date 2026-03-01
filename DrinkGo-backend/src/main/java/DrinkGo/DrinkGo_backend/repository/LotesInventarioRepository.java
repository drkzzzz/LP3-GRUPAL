package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



import DrinkGo.DrinkGo_backend.entity.LotesInventario;
import DrinkGo.DrinkGo_backend.entity.Productos;
import DrinkGo.DrinkGo_backend.entity.Almacenes;
import java.util.List;

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
=======
    /**
     * Obtiene lotes con stock disponible ordenados por FIFO (fecha de ingreso más antigua primero)
     * Usado para salidas de inventario aplicando First In, First Out
     */
    @Query("SELECT l FROM LotesInventario l WHERE l.producto = :producto AND l.almacen = :almacen " +
           "AND l.cantidadActual > 0 AND l.estaActivo = true " +
           "ORDER BY l.fechaIngreso ASC, l.id ASC")
    List<LotesInventario> findLotesDisponiblesFIFO(@Param("producto") Productos producto, @Param("almacen") Almacenes almacen);

    /**
     * Obtiene lotes próximos a vencer
     */
    @Query("SELECT l FROM LotesInventario l WHERE l.almacen = :almacen " +
           "AND l.fechaVencimiento BETWEEN CURRENT_DATE AND :fechaLimite " +
           "AND l.cantidadActual > 0 AND l.estaActivo = true " +
           "ORDER BY l.fechaVencimiento ASC")
    List<LotesInventario> findLotesProximosAVencer(@Param("almacen") Almacenes almacen, @Param("fechaLimite") java.time.LocalDate fechaLimite);

    /**
     * Busca lotes de un producto en un almacén específico
     */
    @Query("SELECT l FROM LotesInventario l WHERE l.producto = :producto AND l.almacen = :almacen AND l.estaActivo = true")
    List<LotesInventario> findByProductoAndAlmacen(@Param("producto") Productos producto, @Param("almacen") Almacenes almacen);
}
