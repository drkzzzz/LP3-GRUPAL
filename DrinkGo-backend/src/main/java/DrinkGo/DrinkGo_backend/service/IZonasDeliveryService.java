package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.ZonasDelivery;

public interface IZonasDeliveryService {
    List<ZonasDelivery> buscarTodos();

    void guardar(ZonasDelivery zonasDelivery);

    void modificar(ZonasDelivery zonasDelivery);

    Optional<ZonasDelivery> buscarId(Long id);

    void eliminar(Long id);
}
