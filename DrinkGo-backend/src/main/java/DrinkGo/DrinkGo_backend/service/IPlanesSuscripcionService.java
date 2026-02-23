package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.PlanesSuscripcion;

public interface IPlanesSuscripcionService {
    List<PlanesSuscripcion> buscarTodos();

    void guardar(PlanesSuscripcion planesSuscripcion);

    void modificar(PlanesSuscripcion planesSuscripcion);

    Optional<PlanesSuscripcion> buscarId(Long id);

    void eliminar(Long id);
}
