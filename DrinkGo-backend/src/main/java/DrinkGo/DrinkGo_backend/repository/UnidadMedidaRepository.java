package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {

    List<UnidadMedida> findByNegocioId(Long negocioId);

    List<UnidadMedida> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    Optional<UnidadMedida> findByNegocioIdAndCodigo(Long negocioId, String codigo);

    List<UnidadMedida> findByNegocioIdAndTipo(Long negocioId, UnidadMedida.TipoUnidadMedida tipo);
}
