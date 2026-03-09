package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.Usuarios;
import java.time.LocalDateTime;
import java.util.List;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    List<Usuarios> findByNegocio_Id(Long negocioId);

    long countByNegocio_Id(Long negocioId);

    @Query("SELECT u FROM Usuarios u JOIN FETCH u.negocio WHERE u.email = :email")
    java.util.Optional<Usuarios> findByEmailWithNegocio(
            @Param("email") String email);

    @Query("SELECT u FROM Usuarios u JOIN FETCH u.negocio WHERE u.bloqueadoHasta IS NOT NULL AND u.bloqueadoHasta > :ahora")
    List<Usuarios> findBloqueados(@Param("ahora") LocalDateTime ahora);

    @Query("SELECT u FROM Usuarios u JOIN FETCH u.negocio")
    List<Usuarios> findAllWithNegocio();
}
