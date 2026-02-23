package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.DetalleOrdenesCompra;

public interface IDetalleOrdenesCompraService {
    List<DetalleOrdenesCompra> buscarTodos();

    void guardar(DetalleOrdenesCompra detalleOrdenesCompra);

    void modificar(DetalleOrdenesCompra detalleOrdenesCompra);

    Optional<DetalleOrdenesCompra> buscarId(Long id);

    void eliminar(Long id);
}
