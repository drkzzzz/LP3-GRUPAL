package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Negocio
 */
@Repository
public interface NegocioRepository extends JpaRepository<Negocio, Long> {

    Optional<Negocio> findByUuid(String uuid);

    Optional<Negocio> findByRuc(String ruc);

    List<Negocio> findByEstado(Negocio.EstadoNegocio estado);

    List<Negocio> findByEstaActivoTrue();

    boolean existsByRuc(String ruc);

    boolean existsByEmail(String email);
}
