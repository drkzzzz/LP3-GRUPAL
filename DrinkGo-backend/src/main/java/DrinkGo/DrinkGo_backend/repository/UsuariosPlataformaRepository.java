package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.UsuariosPlataforma;
import java.util.Optional;

public interface UsuariosPlataformaRepository extends JpaRepository<UsuariosPlataforma, Long> {
    Optional<UsuariosPlataforma> findByEmail(String email);
}
