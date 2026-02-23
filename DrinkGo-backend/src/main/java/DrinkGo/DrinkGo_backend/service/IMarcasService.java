package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Marcas;

public interface IMarcasService {
    List<Marcas> buscarTodos();

    void guardar(Marcas marcas);

    void modificar(Marcas marcas);

    Optional<Marcas> buscarId(Long id);

    void eliminar(Long id);
}
