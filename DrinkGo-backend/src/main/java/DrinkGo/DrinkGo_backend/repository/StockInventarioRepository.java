package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.StockInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para stock de inventario (RF-INV-001).
 * Todas las consultas filtran por negocio_id (multi-tenant obligatorio).
 */
@Repository
public interface StockInventarioRepository extends JpaRepository<StockInventario, Long> {

    /** Listar todo el stock de un negocio */
    List<StockInventario> findByNegocioId(Long negocioId);

    /** Buscar stock por ID y negocio */
    Optional<StockInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /** Buscar stock por producto y almacén dentro del negocio */
    Optional<StockInventario> findByProductoIdAndAlmacenIdAndNegocioId(Long productoId, Long almacenId, Long negocioId);

    /** Listar stock de un producto en todos los almacenes del negocio */
    List<StockInventario> findByProductoIdAndNegocioId(Long productoId, Long negocioId);

    /** Listar stock de un almacén específico */
    List<StockInventario> findByAlmacenIdAndNegocioId(Long almacenId, Long negocioId);
}
