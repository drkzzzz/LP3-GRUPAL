package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.OrdenesCompra;

public interface IOrdenesCompraService {
    List<OrdenesCompra> buscarTodos();
    void guardar(OrdenesCompra ordenesCompra);
    void modificar(OrdenesCompra ordenesCompra);
    Optional<OrdenesCompra> buscarId(Long id);
    void eliminar(Long id);
}
