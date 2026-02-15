package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.SesionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SesionUsuarioRepository extends JpaRepository<SesionUsuario, Long> {
    
    Optional<SesionUsuario> findByTokenAndActivoTrue(String token);
    
    List<SesionUsuario> findByUsuarioIdAndActivoTrue(Long usuarioId);
    
    @Modifying
    @Query("UPDATE SesionUsuario s SET s.activo = false WHERE s.token = :token")
    void desactivarSesion(@Param("token") String token);
    
    @Modifying
    @Query("UPDATE SesionUsuario s SET s.activo = false WHERE s.usuarioId = :usuarioId")
    void desactivarTodasLasSesiones(@Param("usuarioId") Long usuarioId);
    
    @Modifying
    @Query("UPDATE SesionUsuario s SET s.activo = false WHERE s.expiraEn < :fecha")
    void desactivarSesionesExpiradas(@Param("fecha") OffsetDateTime fecha);
    
    @Query("SELECT s FROM SesionUsuario s WHERE s.activo = true AND s.expiraEn > :fecha")
    List<SesionUsuario> findSesionesActivas(@Param("fecha") OffsetDateTime fecha);
    
    long countByUsuarioIdAndActivoTrue(Long usuarioId);
    
    boolean existsByTokenAndActivoTrue(String token);
}
