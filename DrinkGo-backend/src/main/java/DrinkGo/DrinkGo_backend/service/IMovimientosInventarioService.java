package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.MovimientosInventario;

public interface IMovimientosInventarioService {
    List<MovimientosInventario> buscarTodos();

    void guardar(MovimientosInventario movimientosInventario);

    void modificar(MovimientosInventario movimientosInventario);

    Optional<MovimientosInventario> buscarId(Long id);

    void eliminar(Long id);
}
