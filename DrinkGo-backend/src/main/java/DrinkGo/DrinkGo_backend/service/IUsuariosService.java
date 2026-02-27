package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Usuarios;

public interface IUsuariosService {
    List<Usuarios> buscarTodos();

    List<Usuarios> buscarPorNegocio(Long negocioId);

    void guardar(Usuarios usuarios);

    void modificar(Usuarios usuarios);

    Optional<Usuarios> buscarId(Long id);

    void eliminar(Long id);
}
