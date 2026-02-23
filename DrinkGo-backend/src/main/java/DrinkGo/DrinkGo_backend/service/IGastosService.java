package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Gastos;

public interface IGastosService {
    List<Gastos> buscarTodos();

    void guardar(Gastos gastos);

    void modificar(Gastos gastos);

    Optional<Gastos> buscarId(Long id);

    void eliminar(Long id);
}
