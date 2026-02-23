package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Mesas;

public interface IMesasService {
    List<Mesas> buscarTodos();

    void guardar(Mesas mesas);

    void modificar(Mesas mesas);

    Optional<Mesas> buscarId(Long id);

    void eliminar(Long id);
}
