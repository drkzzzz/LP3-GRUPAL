package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.RolesPermisos;

public interface IRolesPermisosService {
    List<RolesPermisos> buscarTodos();

    List<RolesPermisos> buscarPorRol(Long rolId);

    void guardar(RolesPermisos rolesPermisos);

    void modificar(RolesPermisos rolesPermisos);

    Optional<RolesPermisos> buscarId(Long id);

    void eliminar(Long id);
}
