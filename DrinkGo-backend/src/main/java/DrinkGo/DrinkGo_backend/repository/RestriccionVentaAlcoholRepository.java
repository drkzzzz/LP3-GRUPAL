package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.RestriccionVentaAlcohol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestriccionVentaAlcoholRepository extends JpaRepository<RestriccionVentaAlcohol, Long> {
    List<RestriccionVentaAlcohol> findByNegocioId(Long negocioId);

    List<RestriccionVentaAlcohol> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    List<RestriccionVentaAlcohol> findBySedeId(Long sedeId);

    List<RestriccionVentaAlcohol> findByNegocioIdAndSedeIdIsNull(Long negocioId);

    List<RestriccionVentaAlcohol> findByNegocioIdAndTipoRestriccion(Long negocioId, String tipoRestriccion);
}
