package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de referencia para productos.
 * Utilizado para validación en operaciones del Bloque 5.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /** Buscar producto por ID y negocio (validación multi-tenant) */
    Optional<Producto> findByIdAndNegocioId(Long id, Long negocioId);
}
