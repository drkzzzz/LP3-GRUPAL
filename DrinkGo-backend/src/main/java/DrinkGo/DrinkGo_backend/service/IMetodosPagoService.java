package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.MetodosPago;

public interface IMetodosPagoService {
    List<MetodosPago> buscarTodos();

    void guardar(MetodosPago metodosPago);

    void modificar(MetodosPago metodosPago);

    Optional<MetodosPago> buscarId(Long id);

    void eliminar(Long id);
}
