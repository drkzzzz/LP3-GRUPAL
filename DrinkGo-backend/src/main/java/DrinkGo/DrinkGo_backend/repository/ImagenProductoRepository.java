package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.ImagenProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {

    List<ImagenProducto> findByProductoId(Long productoId);

    List<ImagenProducto> findByProductoIdOrderByOrdenAsc(Long productoId);

    Optional<ImagenProducto> findByProductoIdAndEsPrincipal(Long productoId, Boolean esPrincipal);

    void deleteByProductoId(Long productoId);
}
