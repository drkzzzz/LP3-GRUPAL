package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByNegocioId(Long negocioId);

    List<Categoria> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    List<Categoria> findByNegocioIdAndPadreIdIsNull(Long negocioId);

    List<Categoria> findByNegocioIdAndPadreId(Long negocioId, Long padreId);

    List<Categoria> findByNegocioIdAndVisibleTiendaOnline(Long negocioId, Boolean visibleTiendaOnline);

    Optional<Categoria> findByNegocioIdAndSlug(Long negocioId, String slug);

    List<Categoria> findByNegocioIdOrderByOrdenAsc(Long negocioId);
}
