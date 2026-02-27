package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.Usuarios;
import java.util.List;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    List<Usuarios> findByNegocio_Id(Long negocioId);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM Usuarios u JOIN FETCH u.negocio WHERE u.email = :email")
    java.util.Optional<Usuarios> findByEmailWithNegocio(
            @org.springframework.data.repository.query.Param("email") String email);
}
