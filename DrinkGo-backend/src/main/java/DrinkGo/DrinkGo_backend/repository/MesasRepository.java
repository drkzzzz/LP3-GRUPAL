package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.Mesas;
import java.util.List;

public interface MesasRepository extends JpaRepository<Mesas, Long> {

    @Query(value = "SELECT * FROM mesas WHERE sede_id IN (SELECT id FROM sedes WHERE negocio_id = :negocioId AND esta_activo = 1)", nativeQuery = true)
    List<Mesas> findByNegocioId(@Param("negocioId") Long negocioId);
}
