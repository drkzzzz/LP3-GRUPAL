package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.StockInventario;

public interface IStockInventarioService {
    List<StockInventario> buscarTodos();

    void guardar(StockInventario stockInventario);

    void modificar(StockInventario stockInventario);

    Optional<StockInventario> buscarId(Long id);

    void eliminar(Long id);
}
