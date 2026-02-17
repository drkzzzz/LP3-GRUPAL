package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.UsuarioPlataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para UsuarioPlataforma
 */
@Repository
public interface UsuarioPlataformaRepository extends JpaRepository<UsuarioPlataforma, Long> {

    Optional<UsuarioPlataforma> findByEmail(String email);

    Optional<UsuarioPlataforma> findByUuid(String uuid);

    List<UsuarioPlataforma> findByRol(UsuarioPlataforma.RolPlataforma rol);

    List<UsuarioPlataforma> findByEstaActivoTrue();

    boolean existsByEmail(String email);
}
