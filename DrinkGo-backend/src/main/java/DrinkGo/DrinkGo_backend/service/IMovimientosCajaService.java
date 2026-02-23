package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.MovimientosCaja;

public interface IMovimientosCajaService {
    List<MovimientosCaja> buscarTodos();
    void guardar(MovimientosCaja movimientosCaja);
    void modificar(MovimientosCaja movimientosCaja);
    Optional<MovimientosCaja> buscarId(Long id);
    void eliminar(Long id);
}
