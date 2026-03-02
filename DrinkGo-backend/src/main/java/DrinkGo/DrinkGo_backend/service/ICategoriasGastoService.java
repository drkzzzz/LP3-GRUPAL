package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.CategoriasGasto;

public interface ICategoriasGastoService {
    List<CategoriasGasto> buscarTodos();

    List<CategoriasGasto> buscarPorNegocio(Long negocioId);

    void guardar(CategoriasGasto categoriasGasto);

    void modificar(CategoriasGasto categoriasGasto);

    Optional<CategoriasGasto> buscarId(Long id);

    void eliminar(Long id);
}
