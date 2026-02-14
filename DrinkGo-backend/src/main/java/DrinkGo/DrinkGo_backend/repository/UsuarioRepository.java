package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de usuarios - Reemplaza RegistroRepository.
 * Opera sobre la tabla 'usuarios' de drinkgo_database.sql.
 * El @SQLRestriction en la entidad filtra automáticamente los eliminados.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUuid(String uuid);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByNegocioIdAndEmail(Long negocioId, String email);

    boolean existsByEmail(String email);

    boolean existsByNegocioIdAndEmail(Long negocioId, String email);

    java.util.List<Usuario> findByNegocioId(Long negocioId);

    java.util.Optional<Usuario> findByIdAndNegocioId(Long id, Long negocioId);
}
