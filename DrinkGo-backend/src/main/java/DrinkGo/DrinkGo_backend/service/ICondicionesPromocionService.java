package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.CondicionesPromocion;

public interface ICondicionesPromocionService {
    List<CondicionesPromocion> buscarTodos();

    void guardar(CondicionesPromocion condicionesPromocion);

    void modificar(CondicionesPromocion condicionesPromocion);

    Optional<CondicionesPromocion> buscarId(Long id);

    void eliminar(Long id);
}
