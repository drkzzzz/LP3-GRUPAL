package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Combos;

public interface ICombosService {
    List<Combos> buscarTodos();

    void guardar(Combos combos);

    void modificar(Combos combos);

    Optional<Combos> buscarId(Long id);

    void eliminar(Long id);
}
