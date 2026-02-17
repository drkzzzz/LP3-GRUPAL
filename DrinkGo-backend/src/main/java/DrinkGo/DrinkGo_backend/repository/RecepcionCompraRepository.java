package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.RecepcionCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para RecepcionCompra
 */
@Repository
public interface RecepcionCompraRepository extends JpaRepository<RecepcionCompra, Long> {

    List<RecepcionCompra> findByNegocioId(Long negocioId);

    List<RecepcionCompra> findByOrdenCompraId(Long ordenCompraId);

    Optional<RecepcionCompra> findByNegocioIdAndNumeroRecepcion(Long negocioId, String numeroRecepcion);

    List<RecepcionCompra> findByNegocioIdAndFechaRecepcionBetween(Long negocioId, LocalDate fechaInicio, LocalDate fechaFin);

    boolean existsByNegocioIdAndNumeroRecepcion(Long negocioId, String numeroRecepcion);
}
