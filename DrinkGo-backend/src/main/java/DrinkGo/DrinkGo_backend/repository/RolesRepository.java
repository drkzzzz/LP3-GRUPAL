package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.Roles;
import java.util.List;

public interface RolesRepository extends JpaRepository<Roles, Long> {
    List<Roles> findByNegocio_Id(Long negocioId);
}
