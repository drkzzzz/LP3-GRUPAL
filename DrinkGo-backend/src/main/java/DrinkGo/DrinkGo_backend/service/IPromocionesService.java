package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Promociones;

public interface IPromocionesService {
    List<Promociones> buscarTodos();
    void guardar(Promociones promociones);
    void modificar(Promociones promociones);
    Optional<Promociones> buscarId(Long id);
    void eliminar(Long id);
}
