package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.SesionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de sesiones de usuario.
 * Opera sobre la tabla 'sesiones_usuario' adaptada para MySQL (XAMPP).
 */
@Repository
public interface SesionUsuarioRepository extends JpaRepository<SesionUsuario, Long> {

    // --- Consultas Básicas ---
    
    Optional<SesionUsuario> findByHashTokenAndEstaActivo(String hashToken, Boolean estaActivo);

    List<SesionUsuario> findByUsuarioIdAndEstaActivo(Long usuarioId, Boolean estaActivo);

    boolean existsByHashTokenAndEstaActivo(String hashToken, Boolean estaActivo);

    long countByUsuarioIdAndEstaActivo(Long usuarioId, Boolean estaActivo);

    // --- Operaciones de Desactivación (Lógica de Negocio) ---

    @Modifying
    @Query("UPDATE SesionUsuario s SET s.estaActivo = false WHERE s.hashToken = :hashToken")
    void desactivarSesion(@Param("hashToken") String hashToken);

    @Modifying
    @Query("UPDATE SesionUsuario s SET s.estaActivo = false WHERE s.usuarioId = :usuarioId")
    void desactivarTodasLasSesiones(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("UPDATE SesionUsuario s SET s.estaActivo = false WHERE s.expiraEn < :fecha")
    void desactivarSesionesExpiradas(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT s FROM SesionUsuario s WHERE s.estaActivo = true AND s.expiraEn > :fecha")
    List<SesionUsuario> findSesionesActivas(@Param("fecha") LocalDateTime fecha);
}