package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Clientes;

public interface IClientesService {
    List<Clientes> buscarTodos();

    void guardar(Clientes clientes);

    void modificar(Clientes clientes);

    Optional<Clientes> buscarId(Long id);

    void eliminar(Long id);
}
