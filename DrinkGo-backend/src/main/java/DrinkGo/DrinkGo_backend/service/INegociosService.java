package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Negocios;

public interface INegociosService {
    List<Negocios> buscarTodos();

    void guardar(Negocios negocios);

    void modificar(Negocios negocios);

    Optional<Negocios> buscarId(Long id);

    void eliminar(Long id);
}
