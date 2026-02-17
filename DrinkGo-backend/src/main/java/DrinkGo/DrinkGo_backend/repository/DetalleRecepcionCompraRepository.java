package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.DetalleRecepcionCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para DetalleRecepcionCompra
 */
@Repository
public interface DetalleRecepcionCompraRepository extends JpaRepository<DetalleRecepcionCompra, Long> {

    List<DetalleRecepcionCompra> findByRecepcionId(Long recepcionId);

    List<DetalleRecepcionCompra> findByProductoId(Long productoId);

    List<DetalleRecepcionCompra> findByDetalleOrdenCompraId(Long detalleOrdenCompraId);

    void deleteByRecepcionId(Long recepcionId);
}
