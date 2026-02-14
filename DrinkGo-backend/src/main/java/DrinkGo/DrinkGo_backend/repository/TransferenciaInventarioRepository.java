package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.TransferenciaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferenciaInventarioRepository extends JpaRepository<TransferenciaInventario, Long> {

    List<TransferenciaInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    Optional<TransferenciaInventario> findByIdAndNegocioId(Long id, Long negocioId);

    List<TransferenciaInventario> findByNegocioIdAndEstadoOrderByCreadoEnDesc(
            Long negocioId, TransferenciaInventario.TransferenciaEstado estado);

    long countByNegocioId(Long negocioId);
}
