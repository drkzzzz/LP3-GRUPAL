package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.PlantillaNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantillaNotificacionRepository extends JpaRepository<PlantillaNotificacion, Long> {
    List<PlantillaNotificacion> findByNegocioId(Long negocioId);

    List<PlantillaNotificacion> findByNegocioIdIsNull(); // Plantillas globales

    Optional<PlantillaNotificacion> findByCodigo(String codigo);

    List<PlantillaNotificacion> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    List<PlantillaNotificacion> findByCanal(String canal);
}
