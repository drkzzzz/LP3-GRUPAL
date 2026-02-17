package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DrinkGo.DrinkGo_backend.entity.OrdenCompra;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    List<OrdenCompra> findByNegocioId(Long negocioId);

    Optional<OrdenCompra> findByIdAndNegocioId(Long id, Long negocioId);

    boolean existsByNegocioIdAndNumeroOrden(Long negocioId, String numeroOrden);

    List<OrdenCompra> findByNegocioIdAndProveedorId(Long negocioId, Long proveedorId);

    List<OrdenCompra> findByNegocioIdAndEstado(Long negocioId, OrdenCompra.EstadoOrden estado);
}