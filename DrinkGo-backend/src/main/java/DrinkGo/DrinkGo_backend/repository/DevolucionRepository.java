package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de devoluciones.
 */
@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {

    List<Devolucion> findByNegocioId(Long negocioId);

    List<Devolucion> findByNegocioIdAndEstado(Long negocioId, Devolucion.EstadoDevolucion estado);

    Optional<Devolucion> findByNumeroDevolucion(String numeroDevolucion);

    List<Devolucion> findByVentaId(Long ventaId);

    List<Devolucion> findByPedidoId(Long pedidoId);

    List<Devolucion> findByClienteId(Long clienteId);

    List<Devolucion> findByEstado(Devolucion.EstadoDevolucion estado);
}
