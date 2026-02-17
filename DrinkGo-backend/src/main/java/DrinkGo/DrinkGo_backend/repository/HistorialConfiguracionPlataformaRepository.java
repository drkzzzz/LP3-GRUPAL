package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.HistorialConfiguracionPlataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para HistorialConfiguracionPlataforma
 */
@Repository
public interface HistorialConfiguracionPlataformaRepository extends JpaRepository<HistorialConfiguracionPlataforma, Long> {

    List<HistorialConfiguracionPlataforma> findByConfiguracionId(Long configuracionId);

    List<HistorialConfiguracionPlataforma> findByConfiguracionIdOrderByCreadoEnDesc(Long configuracionId);

    List<HistorialConfiguracionPlataforma> findByCambiadoPor(Long cambiadoPor);
}
