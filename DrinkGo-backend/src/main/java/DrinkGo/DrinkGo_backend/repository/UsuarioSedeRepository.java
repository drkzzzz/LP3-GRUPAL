package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.UsuarioSede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioSedeRepository extends JpaRepository<UsuarioSede, Long> {
    List<UsuarioSede> findByUsuarioId(Long usuarioId);

    List<UsuarioSede> findBySedeId(Long sedeId);

    Optional<UsuarioSede> findByUsuarioIdAndSedeId(Long usuarioId, Long sedeId);

    Optional<UsuarioSede> findByUsuarioIdAndEsPredeterminado(Long usuarioId, Boolean esPredeterminado);
}
