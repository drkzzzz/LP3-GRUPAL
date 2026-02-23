package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.ModulosSistema;

public interface IModulosSistemaService {
    List<ModulosSistema> buscarTodos();

    void guardar(ModulosSistema modulosSistema);

    void modificar(ModulosSistema modulosSistema);

    Optional<ModulosSistema> buscarId(Long id);

    void eliminar(Long id);
}
