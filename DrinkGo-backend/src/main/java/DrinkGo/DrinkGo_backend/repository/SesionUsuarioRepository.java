package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.SesionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositorio para sesiones de usuario.
 * Almacena el hash SHA-256 del token JWT (nunca el token plano).
 */
@Repository
public interface SesionUsuarioRepository extends JpaRepository<SesionUsuario, Long> {

    /**
     * Busca una sesión activa por hash del token que no haya expirado.
     */
    Optional<SesionUsuario> findByHashTokenAndEstaActivoTrueAndExpiraEnAfter(
            String hashToken, LocalDateTime ahora);
}
