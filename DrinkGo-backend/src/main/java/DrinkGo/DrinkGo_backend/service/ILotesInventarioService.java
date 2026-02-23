package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.LotesInventario;

public interface ILotesInventarioService {
    List<LotesInventario> buscarTodos();

    void guardar(LotesInventario lotesInventario);

    void modificar(LotesInventario lotesInventario);

    Optional<LotesInventario> buscarId(Long id);

    void eliminar(Long id);
}
