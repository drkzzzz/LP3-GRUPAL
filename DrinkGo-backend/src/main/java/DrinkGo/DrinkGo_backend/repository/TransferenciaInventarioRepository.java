package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.TransferenciaInventario;
import DrinkGo.DrinkGo_backend.entity.TransferenciaInventario.TransferenciaEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para transferencias entre almacenes (RF-INV-005).
 * Mantiene la trazabilidad de documentos de movimiento de stock.
 */
@Repository
public interface TransferenciaInventarioRepository extends JpaRepository<TransferenciaInventario, Long> {

    /** Listar todas las transferencias de un negocio ordenadas por la más reciente */
    List<TransferenciaInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    /** Buscar transferencia por ID y negocio (Seguridad multi-tenant) */
    Optional<TransferenciaInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /** * Listar transferencias filtrando por su estado (ej: borrador, en_transito, recibida).
     */
    List<TransferenciaInventario> findByNegocioIdAndEstadoOrderByCreadoEnDesc(
            Long negocioId, TransferenciaEstado estado);

    /** * Verificar si ya existe el número de documento de transferencia en el negocio.
     * Útil para validaciones antes de guardar.
     */
    boolean existsByNegocioIdAndNumeroTransferencia(Long negocioId, String numeroTransferencia);

    /** Conteo total de transferencias realizadas por el negocio */
    long countByNegocioId(Long negocioId);
}