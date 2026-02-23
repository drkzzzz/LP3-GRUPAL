package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.SeguimientoPedidos;

public interface ISeguimientoPedidosService {
    List<SeguimientoPedidos> buscarTodos();

    void guardar(SeguimientoPedidos seguimientoPedidos);

    void modificar(SeguimientoPedidos seguimientoPedidos);

    Optional<SeguimientoPedidos> buscarId(Long id);

    void eliminar(Long id);
}
