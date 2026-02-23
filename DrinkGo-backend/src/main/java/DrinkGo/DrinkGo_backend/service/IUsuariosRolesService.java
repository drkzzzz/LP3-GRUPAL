package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.UsuariosRoles;

public interface IUsuariosRolesService {
    List<UsuariosRoles> buscarTodos();

    void guardar(UsuariosRoles usuariosRoles);

    void modificar(UsuariosRoles usuariosRoles);

    Optional<UsuariosRoles> buscarId(Long id);

    void eliminar(Long id);
}
