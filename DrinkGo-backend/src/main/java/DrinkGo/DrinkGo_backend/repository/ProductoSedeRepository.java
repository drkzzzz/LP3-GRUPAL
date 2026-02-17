package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.ProductoSede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoSedeRepository extends JpaRepository<ProductoSede, Long> {

    List<ProductoSede> findByProductoId(Long productoId);

    List<ProductoSede> findBySedeId(Long sedeId);

    List<ProductoSede> findBySedeIdAndEstaDisponible(Long sedeId, Boolean estaDisponible);

    Optional<ProductoSede> findByProductoIdAndSedeId(Long productoId, Long sedeId);

    void deleteByProductoId(Long productoId);

    void deleteBySedeId(Long sedeId);
}
