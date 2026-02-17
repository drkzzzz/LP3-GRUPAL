package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para UsuarioRol
 */
@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    List<UsuarioRol> findByUsuarioId(Long usuarioId);

    List<UsuarioRol> findByRolId(Long rolId);

    Optional<UsuarioRol> findByUsuarioIdAndRolId(Long usuarioId, Long rolId);

    boolean existsByUsuarioIdAndRolId(Long usuarioId, Long rolId);

    List<UsuarioRol> findByValidoHastaIsNullOrValidoHastaAfter(LocalDateTime fecha);

    List<UsuarioRol> findByUsuarioIdAndValidoHastaIsNullOrValidoHastaAfter(Long usuarioId, LocalDateTime fecha);

    void deleteByUsuarioId(Long usuarioId);

    void deleteByUsuarioIdAndRolId(Long usuarioId, Long rolId);
}
