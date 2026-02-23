package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.ProductosProveedor;

public interface IProductosProveedorService {
    List<ProductosProveedor> buscarTodos();

    void guardar(ProductosProveedor productosProveedor);

    void modificar(ProductosProveedor productosProveedor);

    Optional<ProductosProveedor> buscarId(Long id);

    void eliminar(Long id);
}
