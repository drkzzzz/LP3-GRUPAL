package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de referencia para almacenes.
 * Utilizado para validación en operaciones del Bloque 5.
 */
@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {

    /** Buscar almacén por ID y negocio (validación multi-tenant) */
    Optional<Almacen> findByIdAndNegocioId(Long id, Long negocioId);
}
