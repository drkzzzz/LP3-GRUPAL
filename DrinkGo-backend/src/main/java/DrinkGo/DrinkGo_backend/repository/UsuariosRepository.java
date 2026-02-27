package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.Usuarios;
import java.util.List;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    List<Usuarios> findByNegocio_Id(Long negocioId);
}
