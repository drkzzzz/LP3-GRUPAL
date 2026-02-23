package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.UnidadesMedida;

public interface IUnidadesMedidaService {
    List<UnidadesMedida> buscarTodos();

    void guardar(UnidadesMedida unidadesMedida);

    void modificar(UnidadesMedida unidadesMedida);

    Optional<UnidadesMedida> buscarId(Long id);

    void eliminar(Long id);
}
