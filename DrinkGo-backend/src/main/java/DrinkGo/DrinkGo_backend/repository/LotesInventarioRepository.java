package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.LotesInventario;
import DrinkGo.DrinkGo_backend.entity.Productos;
import DrinkGo.DrinkGo_backend.entity.Almacenes;
import java.util.List;

public interface LotesInventarioRepository extends JpaRepository<LotesInventario, Long> {

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
