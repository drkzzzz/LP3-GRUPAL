package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Producto - Bloque 4 (Catálogo de Productos).
 * Soporta multi-tenant mediante negocio_id.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Listar todos los productos de un negocio.
     * El @Where de la entidad ya filtra eliminado_en IS NULL.
     */
    List<Producto> findByNegocioId(Long negocioId);

    /**
     * Buscar producto por ID y negocio (validación multi-tenant).
     */
    Optional<Producto> findByIdAndNegocioId(Long id, Long negocioId);

    /**
     * Verificar si existe un producto con el mismo SKU en el negocio.
     */
    boolean existsByNegocioIdAndSku(Long negocioId, String sku);

    /**
     * Verificar si existe un producto con el mismo código de barras en el negocio.
     */
    boolean existsByNegocioIdAndCodigoBarras(Long negocioId, String codigoBarras);
}

