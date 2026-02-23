package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.ModulosNegocio;

public interface IModulosNegocioService {
    List<ModulosNegocio> buscarTodos();

    void guardar(ModulosNegocio modulosNegocio);

    void modificar(ModulosNegocio modulosNegocio);

    Optional<ModulosNegocio> buscarId(Long id);

    void eliminar(Long id);
}
