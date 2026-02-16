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

/**
 * Repositorio para lotes de inventario (RF-INV-002..003).
 * Garantiza el cumplimiento del sistema FIFO (First-In, First-Out).
 */
@Repository
public interface LoteInventarioRepository extends JpaRepository<LoteInventario, Long> {

    /** Listar todos los lotes de un negocio */
    List<LoteInventario> findByNegocioId(Long negocioId);

    /** Buscar lote por ID y negocio (Seguridad multi-tenant) */
    Optional<LoteInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /**
     * CONSULTA FIFO MAESTRA:
     * Obtiene lotes disponibles ordenados por fecha de recepción (más antiguos primero).
     * Filtra automáticamente lotes agotados y lotes cuya fecha de vencimiento ya pasó.
     */
    @Query("SELECT l FROM LoteInventario l WHERE l.negocioId = :negocioId " +
           "AND l.productoId = :productoId AND l.almacenId = :almacenId " +
           "AND l.estado = :estado AND l.cantidadRestante > 0 " +
           "AND (l.fechaVencimiento IS NULL OR l.fechaVencimiento >= :hoy) " +
           "ORDER BY l.fechaRecepcion ASC, l.id ASC")
    List<LoteInventario> findLotesFIFODisponibles(
            @Param("negocioId") Long negocioId,
            @Param("productoId") Long productoId,
            @Param("almacenId") Long almacenId,
            @Param("estado") LoteEstado estado,
            @Param("hoy") LocalDate hoy);

    /** Buscar lotes próximos a vencer (útil para el sistema de alertas) */
    @Query("SELECT l FROM LoteInventario l WHERE l.negocioId = :negocioId " +
           "AND l.estado = :estado AND l.fechaVencimiento BETWEEN :desde AND :hasta")
    List<LoteInventario> findProximosAVencer(
            @Param("negocioId") Long negocioId, 
            @Param("estado") LoteEstado estado, 
            @Param("desde") LocalDate desde, 
            @Param("hasta") LocalDate hasta);

    /** Buscar lotes que ya vencieron pero siguen marcados como disponibles en el sistema */
    List<LoteInventario> findByNegocioIdAndEstadoAndFechaVencimientoBefore(
            Long negocioId, LoteEstado estado, LocalDate fecha);

    /** Buscar lotes por producto y negocio */
    List<LoteInventario> findByProductoIdAndNegocioId(Long productoId, Long negocioId);

    /** Verificar si un número de lote ya existe en el negocio para evitar duplicados */
    boolean existsByNegocioIdAndNumeroLote(Long negocioId, String numeroLote);
}