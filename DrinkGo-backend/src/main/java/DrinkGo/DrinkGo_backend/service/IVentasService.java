package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Ventas;

public interface IVentasService {
    List<Ventas> buscarTodos();

    void guardar(Ventas ventas);

    void modificar(Ventas ventas);

    Optional<Ventas> buscarId(Long id);

    void eliminar(Long id);
}
