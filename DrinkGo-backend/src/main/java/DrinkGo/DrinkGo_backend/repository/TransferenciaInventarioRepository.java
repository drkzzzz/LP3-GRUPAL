package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.TransferenciaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para transferencias entre almacenes (RF-INV-005).
 * Todas las consultas filtran por negocio_id (multi-tenant obligatorio).
 */
@Repository
public interface TransferenciaInventarioRepository extends JpaRepository<TransferenciaInventario, Long> {

    /** Listar todas las transferencias de un negocio */
    List<TransferenciaInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    /** Buscar transferencia por ID y negocio */
    Optional<TransferenciaInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /** Verificar existencia de número de transferencia en el negocio */
    boolean existsByNumeroTransferenciaAndNegocioId(String numeroTransferencia, Long negocioId);

    /** Listar transferencias por estado */
    List<TransferenciaInventario> findByNegocioIdAndEstadoOrderByCreadoEnDesc(
            Long negocioId, TransferenciaInventario.EstadoTransferencia estado);
}
