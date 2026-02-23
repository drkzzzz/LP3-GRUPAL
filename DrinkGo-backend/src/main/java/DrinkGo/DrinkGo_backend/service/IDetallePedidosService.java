package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.DetallePedidos;

public interface IDetallePedidosService {
    List<DetallePedidos> buscarTodos();
    void guardar(DetallePedidos detallePedidos);
    void modificar(DetallePedidos detallePedidos);
    Optional<DetallePedidos> buscarId(Long id);
    void eliminar(Long id);
}
