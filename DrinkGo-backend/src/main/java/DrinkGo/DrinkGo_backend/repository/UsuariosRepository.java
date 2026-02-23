package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.Usuarios;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {

}
