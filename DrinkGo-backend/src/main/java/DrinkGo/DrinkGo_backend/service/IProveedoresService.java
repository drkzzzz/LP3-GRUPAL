package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Proveedores;

public interface IProveedoresService {
    List<Proveedores> buscarTodos();

    void guardar(Proveedores proveedores);

    void modificar(Proveedores proveedores);

    Optional<Proveedores> buscarId(Long id);

    void eliminar(Long id);
}
