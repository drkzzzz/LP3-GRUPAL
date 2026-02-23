package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Devoluciones;

public interface IDevolucionesService {
    List<Devoluciones> buscarTodos();
    void guardar(Devoluciones devoluciones);
    void modificar(Devoluciones devoluciones);
    Optional<Devoluciones> buscarId(Long id);
    void eliminar(Long id);
}
