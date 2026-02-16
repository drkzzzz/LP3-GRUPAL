package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.LoteInventario;
import DrinkGo.DrinkGo_backend.entity.LoteInventario.LoteEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteInventarioRepository extends JpaRepository<LoteInventario, Long> {

    List<LoteInventario> findByNegocioIdAndProductoIdOrderByFechaRecepcionAsc(
            Long negocioId, Long productoId);

    List<LoteInventario> findByNegocioIdAndProductoIdAndAlmacenIdOrderByFechaRecepcionAsc(
            Long negocioId, Long productoId, Long almacenId);

    // FIFO: lotes disponibles ordenados por fecha de recepción (más antiguo primero)
    @Query("SELECT l FROM LoteInventario l WHERE l.negocioId = :negocioId " +
            "AND l.productoId = :productoId AND l.almacenId = :almacenId " +
            "AND l.estado = 'disponible' AND l.cantidadRestante > 0 " +
            "ORDER BY l.fechaRecepcion ASC, l.id ASC")
    List<LoteInventario> findLotesDisponiblesFIFO(
            @Param("negocioId") Long negocioId,
            @Param("productoId") Long productoId,
            @Param("almacenId") Long almacenId);

    // Lotes próximos a vencer
    @Query("SELECT l FROM LoteInventario l WHERE l.negocioId = :negocioId " +
            "AND l.estado = 'disponible' AND l.fechaVencimiento IS NOT NULL " +
            "AND l.fechaVencimiento <= :fechaLimite AND l.fechaVencimiento > :fechaHoy")
    List<LoteInventario> findLotesProximosAVencer(
            @Param("negocioId") Long negocioId,
            @Param("fechaHoy") LocalDate fechaHoy,
            @Param("fechaLimite") LocalDate fechaLimite);

    // Lotes ya vencidos (aún con estado disponible)
    @Query("SELECT l FROM LoteInventario l WHERE l.negocioId = :negocioId " +
            "AND l.estado = 'disponible' AND l.fechaVencimiento IS NOT NULL " +
            "AND l.fechaVencimiento < :fechaHoy")
    List<LoteInventario> findLotesVencidos(
            @Param("negocioId") Long negocioId,
            @Param("fechaHoy") LocalDate fechaHoy);

    boolean existsByNegocioIdAndNumeroLote(Long negocioId, String numeroLote);

    List<LoteInventario> findByNegocioId(Long negocioId);
}
