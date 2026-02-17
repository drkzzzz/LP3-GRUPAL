package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.RolPermiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para RolPermiso
 */
@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {

    List<RolPermiso> findByRolId(Long rolId);

    List<RolPermiso> findByPermisoId(Long permisoId);

    Optional<RolPermiso> findByRolIdAndPermisoId(Long rolId, Long permisoId);

    boolean existsByRolIdAndPermisoId(Long rolId, Long permisoId);

    void deleteByRolId(Long rolId);

    void deleteByRolIdAndPermisoId(Long rolId, Long permisoId);
}
