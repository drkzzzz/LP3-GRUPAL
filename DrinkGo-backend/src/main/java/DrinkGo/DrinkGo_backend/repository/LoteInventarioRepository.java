package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.LoteInventario;
import DrinkGo.DrinkGo_backend.entity.LoteInventario.LoteEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteInventarioRepository extends JpaRepository<LoteInventario, Long> {

    List<LoteInventario> findByNegocioId(Long negocioId);

    Optional<LoteInventario> findByIdAndNegocioId(Long id, Long negocioId);

    List<LoteInventario> findByProductoIdAndAlmacenIdAndNegocioId(Long productoId, Long almacenId, Long negocioId);

    List<LoteInventario> findByEstadoAndNegocioId(LoteEstado estado, Long negocioId);

    /**
     * Lotes disponibles para un producto+almacén ordenados FIFO (por id ASC).
     */
    @Query("SELECT l FROM LoteInventario l " +
           "WHERE l.productoId = :productoId AND l.almacenId = :almacenId " +
           "AND l.negocioId = :negocioId AND l.estado = 'disponible' " +
           "AND l.cantidadRestante > 0 " +
           "ORDER BY l.id ASC")
    List<LoteInventario> findLotesDisponiblesFIFO(@Param("productoId") Long productoId,
                                                   @Param("almacenId") Long almacenId,
                                                   @Param("negocioId") Long negocioId);

    /**
     * Lotes próximos a vencer dentro de los días indicados.
     */
    @Query("SELECT l FROM LoteInventario l " +
           "WHERE l.negocioId = :negocioId AND l.estado = 'disponible' " +
           "AND l.fechaVencimiento IS NOT NULL " +
           "AND l.fechaVencimiento <= :fechaLimite " +
           "ORDER BY l.fechaVencimiento ASC")
    List<LoteInventario> findLotesProximosAVencer(@Param("negocioId") Long negocioId,
                                                   @Param("fechaLimite") LocalDate fechaLimite);

    /**
     * Lotes vencidos (estado = disponible pero fecha de vencimiento pasada).
     */
    @Query("SELECT l FROM LoteInventario l " +
           "WHERE l.negocioId = :negocioId AND l.estado = 'disponible' " +
           "AND l.fechaVencimiento IS NOT NULL " +
           "AND l.fechaVencimiento < :hoy")
    List<LoteInventario> findLotesVencidos(@Param("negocioId") Long negocioId,
                                            @Param("hoy") LocalDate hoy);

    boolean existsByNumeroLoteAndNegocioId(String numeroLote, Long negocioId);
}
