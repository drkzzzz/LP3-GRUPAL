package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.ProductoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para ProductoProveedor
 */
@Repository
public interface ProductoProveedorRepository extends JpaRepository<ProductoProveedor, Long> {

    List<ProductoProveedor> findByProveedorId(Long proveedorId);

    List<ProductoProveedor> findByProductoId(Long productoId);

    Optional<ProductoProveedor> findByProveedorIdAndProductoId(Long proveedorId, Long productoId);

    List<ProductoProveedor> findByProductoIdAndEsPreferido(Long productoId, Boolean esPreferido);

    boolean existsByProveedorIdAndProductoId(Long proveedorId, Long productoId);

    void deleteByProveedorId(Long proveedorId);

    void deleteByProductoId(Long productoId);
}
