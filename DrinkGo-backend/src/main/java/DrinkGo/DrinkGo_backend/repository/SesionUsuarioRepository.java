package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.SesionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de sesiones de usuario.
 * Opera sobre la tabla 'sesiones_usuario' de drinkgo_database.sql.
 * Almacena los tokens JWT activos (equivalente al access_token de la clase).
 */
@Repository
public interface SesionUsuarioRepository extends JpaRepository<SesionUsuario, Long> {

    Optional<SesionUsuario> findByHashTokenAndEstaActivo(String hashToken, Boolean estaActivo);

    List<SesionUsuario> findByUsuarioIdAndEstaActivo(Long usuarioId, Boolean estaActivo);

    boolean existsByHashTokenAndEstaActivo(String hashToken, Boolean estaActivo);
}
