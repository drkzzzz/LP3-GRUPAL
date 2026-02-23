package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.UsuariosSedes;

public interface IUsuariosSedesService {
    List<UsuariosSedes> buscarTodos();
    void guardar(UsuariosSedes usuariosSedes);
    void modificar(UsuariosSedes usuariosSedes);
    Optional<UsuariosSedes> buscarId(Long id);
    void eliminar(Long id);
}
