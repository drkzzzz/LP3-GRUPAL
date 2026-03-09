package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.RolesPermisos;
import java.util.List;

public interface RolesPermisosRepository extends JpaRepository<RolesPermisos, Long> {
    List<RolesPermisos> findByRol_Id(Long rolId);
}
