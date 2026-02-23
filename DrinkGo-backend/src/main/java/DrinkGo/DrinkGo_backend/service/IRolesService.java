package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Roles;

public interface IRolesService {
    List<Roles> buscarTodos();
    void guardar(Roles roles);
    void modificar(Roles roles);
    Optional<Roles> buscarId(Long id);
    void eliminar(Long id);
}
