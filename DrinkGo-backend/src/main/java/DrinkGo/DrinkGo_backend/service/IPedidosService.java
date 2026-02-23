package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Pedidos;

public interface IPedidosService {
    List<Pedidos> buscarTodos();

    void guardar(Pedidos pedidos);

    void modificar(Pedidos pedidos);

    Optional<Pedidos> buscarId(Long id);

    void eliminar(Long id);
}
