package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.PermisosSistema;

public interface IPermisosSistemaService {
    List<PermisosSistema> buscarTodos();

    void guardar(PermisosSistema permisosSistema);

    void modificar(PermisosSistema permisosSistema);

    Optional<PermisosSistema> buscarId(Long id);

    void eliminar(Long id);
}
