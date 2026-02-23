package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.SesionesUsuario;

public interface ISesionesUsuarioService {
    List<SesionesUsuario> buscarTodos();

    void guardar(SesionesUsuario sesionesUsuario);

    void modificar(SesionesUsuario sesionesUsuario);

    Optional<SesionesUsuario> buscarId(Long id);

    void eliminar(Long id);
}
