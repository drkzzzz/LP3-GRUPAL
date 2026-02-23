package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.DetalleCombos;

public interface IDetalleCombosService {
    List<DetalleCombos> buscarTodos();
    void guardar(DetalleCombos detalleCombos);
    void modificar(DetalleCombos detalleCombos);
    Optional<DetalleCombos> buscarId(Long id);
    void eliminar(Long id);
}
