package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.DetalleDevolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de detalle de devoluciones.
 */
@Repository
public interface DetalleDevolucionRepository extends JpaRepository<DetalleDevolucion, Long> {

    List<DetalleDevolucion> findByDevolucionId(Long devolucionId);

    List<DetalleDevolucion> findByProductoId(Long productoId);
}
