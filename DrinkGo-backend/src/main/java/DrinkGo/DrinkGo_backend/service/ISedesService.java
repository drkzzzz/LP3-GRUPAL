package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Sedes;

public interface ISedesService {
    List<Sedes> buscarTodos();

    void guardar(Sedes sedes);

    void modificar(Sedes sedes);

    Optional<Sedes> buscarId(Long id);

    void eliminar(Long id);
}
