package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.UnidadesMedida;

public interface UnidadesMedidaRepository extends JpaRepository<UnidadesMedida, Long> {

    List<UnidadesMedida> findByNegocioId(Long negocioId);
}
