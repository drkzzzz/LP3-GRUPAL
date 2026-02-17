package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DrinkGo.DrinkGo_backend.entity.Proveedor;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    List<Proveedor> findByNegocioId(Long negocioId);

    Optional<Proveedor> findByIdAndNegocioId(Long id, Long negocioId);

    boolean existsByNegocioIdAndCodigo(Long negocioId, String codigo);

    boolean existsByNegocioIdAndRuc(Long negocioId, String ruc);
}