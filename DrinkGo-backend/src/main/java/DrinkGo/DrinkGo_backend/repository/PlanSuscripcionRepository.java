package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.PlanSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de planes de suscripción.
 */
@Repository
public interface PlanSuscripcionRepository extends JpaRepository<PlanSuscripcion, Long> {

    List<PlanSuscripcion> findByEstaActivoOrderByOrdenAsc(Boolean estaActivo);

    Optional<PlanSuscripcion> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
