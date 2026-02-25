package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.UsuariosPlataforma;

public interface IUsuariosPlataformaService {
    List<UsuariosPlataforma> buscarTodos();

    void guardar(UsuariosPlataforma usuariosPlataforma);

    void modificar(UsuariosPlataforma usuariosPlataforma);

    Optional<UsuariosPlataforma> buscarId(Long id);

    void eliminar(Long id);

    Optional<UsuariosPlataforma> buscarPorEmail(String email);
}
