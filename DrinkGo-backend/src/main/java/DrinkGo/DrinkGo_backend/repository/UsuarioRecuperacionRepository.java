package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.UsuarioRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface UsuarioRecuperacionRepository extends JpaRepository<UsuarioRecuperacion, Long> {
    
    Optional<UsuarioRecuperacion> findByTokenAndUsadoFalse(String token);
    
    @Query("SELECT ur FROM UsuarioRecuperacion ur WHERE ur.token = :token AND ur.usado = false AND ur.expiraEn > :fecha")
    Optional<UsuarioRecuperacion> findTokenValido(@Param("token") String token, @Param("fecha") OffsetDateTime fecha);
    
    @Modifying
    @Query("UPDATE UsuarioRecuperacion ur SET ur.usado = true WHERE ur.token = :token")
    void marcarComoUsado(@Param("token") String token);
    
    @Modifying
    @Query("DELETE FROM UsuarioRecuperacion ur WHERE ur.expiraEn < :fecha")
    void eliminarTokensExpirados(@Param("fecha") OffsetDateTime fecha);
    
    boolean existsByUsuarioIdAndUsadoFalse(Long usuarioId);
}
