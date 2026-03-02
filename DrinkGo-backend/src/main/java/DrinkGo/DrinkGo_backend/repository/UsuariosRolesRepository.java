package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.UsuariosRoles;
import java.util.List;

public interface UsuariosRolesRepository extends JpaRepository<UsuariosRoles, Long> {

    /**
     * Devuelve los códigos de TODOS los permisos del sistema que tienen los roles
     * del usuario.
     */
    @Query("SELECT rp.permiso.codigo FROM RolesPermisos rp " +
            "WHERE rp.rol.id IN (SELECT ur.rol.id FROM UsuariosRoles ur WHERE ur.usuario.id = :usuarioId)")
    List<String> findCodigosPermisoByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Carga los roles del usuario junto con el objeto Roles (JOIN FETCH)
     * para evitar LazyInitializationException al serializar.
     */
    @Query("SELECT ur FROM UsuariosRoles ur JOIN FETCH ur.rol WHERE ur.usuario.id = :usuarioId")
    List<UsuariosRoles> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
