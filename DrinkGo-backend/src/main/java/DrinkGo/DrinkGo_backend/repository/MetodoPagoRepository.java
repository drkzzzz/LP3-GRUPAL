package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    List<MetodoPago> findByNegocioId(Long negocioId);

    List<MetodoPago> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    Optional<MetodoPago> findByNegocioIdAndCodigo(Long negocioId, String codigo);

    List<MetodoPago> findByNegocioIdAndTipo(Long negocioId, String tipo);

    List<MetodoPago> findByNegocioIdAndDisponiblePos(Long negocioId, Boolean disponiblePos);

    List<MetodoPago> findByNegocioIdAndDisponibleTiendaOnline(Long negocioId, Boolean disponibleTiendaOnline);

    List<MetodoPago> findByNegocioIdOrderByOrdenAsc(Long negocioId);
}
