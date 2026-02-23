package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.DetalleDevoluciones;

public interface IDetalleDevolucionesService {
    List<DetalleDevoluciones> buscarTodos();

    void guardar(DetalleDevoluciones detalleDevoluciones);

    void modificar(DetalleDevoluciones detalleDevoluciones);

    Optional<DetalleDevoluciones> buscarId(Long id);

    void eliminar(Long id);
}
