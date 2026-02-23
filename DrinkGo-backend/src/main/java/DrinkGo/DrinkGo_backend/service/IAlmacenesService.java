package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Almacenes;

public interface IAlmacenesService {
    List<Almacenes> buscarTodos();

    void guardar(Almacenes almacenes);

    void modificar(Almacenes almacenes);

    Optional<Almacenes> buscarId(Long id);

    void eliminar(Long id);
}
