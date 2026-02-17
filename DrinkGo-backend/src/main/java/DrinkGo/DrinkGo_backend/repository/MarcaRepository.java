package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {

    List<Marca> findByNegocioId(Long negocioId);

    List<Marca> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    Optional<Marca> findByNegocioIdAndSlug(Long negocioId, String slug);

    List<Marca> findByNegocioIdAndPaisOrigen(Long negocioId, String paisOrigen);

    List<Marca> findByNegocioIdOrderByNombreAsc(Long negocioId);
}
