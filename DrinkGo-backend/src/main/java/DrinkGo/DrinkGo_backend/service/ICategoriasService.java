package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Categorias;

public interface ICategoriasService {
    List<Categorias> buscarTodos();

    void guardar(Categorias categorias);

    void modificar(Categorias categorias);

    Optional<Categorias> buscarId(Long id);

    void eliminar(Long id);
}
