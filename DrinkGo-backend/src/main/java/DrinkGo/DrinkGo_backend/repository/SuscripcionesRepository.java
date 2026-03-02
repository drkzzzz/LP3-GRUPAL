package DrinkGo.DrinkGo_backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.Suscripciones;

public interface SuscripcionesRepository extends JpaRepository<Suscripciones, Long> {
    Optional<Suscripciones> findFirstByNegocioIdAndEstado(Long negocioId, Suscripciones.EstadoSuscripcion estado);

    Optional<Suscripciones> findFirstByNegocioIdOrderByIdDesc(Long negocioId);
}
