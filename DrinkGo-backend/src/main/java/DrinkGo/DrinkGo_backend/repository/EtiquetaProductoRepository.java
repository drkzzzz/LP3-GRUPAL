package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.EtiquetaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtiquetaProductoRepository extends JpaRepository<EtiquetaProducto, Long> {

    List<EtiquetaProducto> findByNegocioId(Long negocioId);

    Optional<EtiquetaProducto> findByNegocioIdAndSlug(Long negocioId, String slug);

    List<EtiquetaProducto> findByNegocioIdOrderByNombreAsc(Long negocioId);
}
