package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la relación usuario-rol.
 * Utilizado para obtener los roles asignados al usuario autenticado.
 */
@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    /**
     * Obtiene todos los roles asignados a un usuario.
     */
    List<UsuarioRol> findByUsuarioId(Long usuarioId);
}
