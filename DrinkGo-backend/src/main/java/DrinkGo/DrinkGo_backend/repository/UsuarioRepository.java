package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para usuarios del negocio.
 * Utilizado exclusivamente para autenticación y validación de seguridad.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuarios activos por email (puede haber el mismo email en distintos negocios).
     */
    List<Usuario> findByEmailAndEstaActivoTrueAndEliminadoEnIsNull(String email);

    /**
     * Busca un usuario activo por email y negocio específico.
     * Resuelve ambigüedad cuando el mismo email existe en múltiples negocios.
     */
    Optional<Usuario> findByEmailAndNegocioIdAndEstaActivoTrueAndEliminadoEnIsNull(String email, Long negocioId);

    /**
     * Busca un usuario activo por email.
     */
    Optional<Usuario> findFirstByEmailAndEstaActivoTrueAndEliminadoEnIsNull(String email);
}
