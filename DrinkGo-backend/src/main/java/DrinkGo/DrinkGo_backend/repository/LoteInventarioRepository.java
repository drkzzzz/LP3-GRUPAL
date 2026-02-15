package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.LoteInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para lotes de inventario (RF-INV-002..003).
 * Todas las consultas filtran por negocio_id (multi-tenant obligatorio).
 * El orden FIFO se garantiza con fecha_recepcion ASC.
 */
@Repository
public interface LoteInventarioRepository extends JpaRepository<LoteInventario, Long> {

    /** Listar todos los lotes de un negocio */
    List<LoteInventario> findByNegocioId(Long negocioId);

    /** Buscar lote por ID y negocio */
    Optional<LoteInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /**
     * FIFO: Obtener lotes disponibles de un producto en un almacén,
     * ordenados por fecha de recepción ascendente (más antiguos primero).
     * EXCLUYE lotes vencidos (fecha_vencimiento < hoy) para evitar vender producto expirado.
     */
    @Query("SELECT l FROM LoteInventario l WHERE l.productoId = :productoId " +
           "AND l.almacenId = :almacenId AND l.negocioId = :negocioId " +
           "AND l.estado = :estado AND l.cantidadRestante > :cantidadMinima " +
           "AND (l.fechaVencimiento IS NULL OR l.fechaVencimiento >= CURRENT_DATE) " +
           "ORDER BY l.fechaRecepcion ASC")
    List<LoteInventario> findLotesFIFODisponibles(
            @Param("productoId") Long productoId,
            @Param("almacenId") Long almacenId,
            @Param("negocioId") Long negocioId,
            @Param("estado") LoteInventario.EstadoLote estado,
            @Param("cantidadMinima") Integer cantidadMinima);

    /** Buscar lotes próximos a vencer (para alertas) */
    List<LoteInventario> findByNegocioIdAndEstadoAndFechaVencimientoBetween(
            Long negocioId, LoteInventario.EstadoLote estado, LocalDate desde, LocalDate hasta);

    /** Buscar lotes vencidos */
    List<LoteInventario> findByNegocioIdAndEstadoAndFechaVencimientoBefore(
            Long negocioId, LoteInventario.EstadoLote estado, LocalDate fecha);

    /** Buscar lotes por producto y negocio */
    List<LoteInventario> findByProductoIdAndNegocioId(Long productoId, Long negocioId);

    /** Verificar existencia de número de lote en el negocio */
    boolean existsByNumeroLoteAndNegocioId(String numeroLote, Long negocioId);
}
