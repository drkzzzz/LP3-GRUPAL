package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Productos;

public interface IProductosService {
    List<Productos> buscarTodos();
    void guardar(Productos productos);
    void modificar(Productos productos);
    Optional<Productos> buscarId(Long id);
    void eliminar(Long id);
}
