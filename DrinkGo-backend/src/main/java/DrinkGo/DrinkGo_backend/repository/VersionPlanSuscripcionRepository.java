package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.VersionPlanSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para VersionPlanSuscripcion
 */
@Repository
public interface VersionPlanSuscripcionRepository extends JpaRepository<VersionPlanSuscripcion, Long> {

    List<VersionPlanSuscripcion> findByPlanId(Long planId);

    Optional<VersionPlanSuscripcion> findByPlanIdAndVersion(Long planId, Integer version);

    List<VersionPlanSuscripcion> findByPlanIdOrderByVersionDesc(Long planId);
}
