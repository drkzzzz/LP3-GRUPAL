package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Suscripciones;

public interface ISuscripcionesService {
    List<Suscripciones> buscarTodos();

    void guardar(Suscripciones suscripciones);

    void modificar(Suscripciones suscripciones);

    Optional<Suscripciones> buscarId(Long id);

    void eliminar(Long id);
}
