package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.PagosPedido;

public interface IPagosPedidoService {
    List<PagosPedido> buscarTodos();

    void guardar(PagosPedido pagosPedido);

    void modificar(PagosPedido pagosPedido);

    Optional<PagosPedido> buscarId(Long id);

    void eliminar(Long id);
}
