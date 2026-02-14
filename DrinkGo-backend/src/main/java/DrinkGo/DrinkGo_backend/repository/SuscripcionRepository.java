package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de suscripciones.
 */
@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

    Optional<Suscripcion> findByNegocioIdAndEstado(Long negocioId, Suscripcion.EstadoSuscripcion estado);

    List<Suscripcion> findByNegocioId(Long negocioId);

    List<Suscripcion> findByPlanId(Long planId);

    List<Suscripcion> findByEstado(Suscripcion.EstadoSuscripcion estado);
}
