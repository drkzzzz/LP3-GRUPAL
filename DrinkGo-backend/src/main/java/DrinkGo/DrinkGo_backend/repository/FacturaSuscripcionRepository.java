package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.FacturaSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para FacturaSuscripcion
 */
@Repository
public interface FacturaSuscripcionRepository extends JpaRepository<FacturaSuscripcion, Long> {

    Optional<FacturaSuscripcion> findByNumeroFactura(String numeroFactura);

    List<FacturaSuscripcion> findByNegocioId(Long negocioId);

    List<FacturaSuscripcion> findBySuscripcionId(Long suscripcionId);

    List<FacturaSuscripcion> findByEstado(FacturaSuscripcion.EstadoFactura estado);

    List<FacturaSuscripcion> findByNegocioIdAndEstado(Long negocioId, FacturaSuscripcion.EstadoFactura estado);

    List<FacturaSuscripcion> findByFechaVencimientoBefore(LocalDate fecha);

    boolean existsByNumeroFactura(String numeroFactura);
}
