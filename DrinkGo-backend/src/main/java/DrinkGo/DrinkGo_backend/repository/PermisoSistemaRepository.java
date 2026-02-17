package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.PermisoSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para PermisoSistema
 */
@Repository
public interface PermisoSistemaRepository extends JpaRepository<PermisoSistema, Long> {

    Optional<PermisoSistema> findByCodigo(String codigo);

    List<PermisoSistema> findByModuloId(Long moduloId);

    List<PermisoSistema> findByTipoAccion(PermisoSistema.TipoAccion tipoAccion);

    boolean existsByCodigo(String codigo);
}
