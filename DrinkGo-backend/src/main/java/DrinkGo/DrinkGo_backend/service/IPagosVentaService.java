package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.PagosVenta;

public interface IPagosVentaService {
    List<PagosVenta> buscarTodos();

    void guardar(PagosVenta pagosVenta);

    void modificar(PagosVenta pagosVenta);

    Optional<PagosVenta> buscarId(Long id);

    void eliminar(Long id);
}
