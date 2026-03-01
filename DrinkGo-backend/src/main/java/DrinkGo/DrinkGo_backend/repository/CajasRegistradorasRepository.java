package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.CajasRegistradoras;

public interface CajasRegistradorasRepository extends JpaRepository<CajasRegistradoras, Long> {

    List<CajasRegistradoras> findByNegocioId(Long negocioId);

    boolean existsByCodigo(String codigo);
}
