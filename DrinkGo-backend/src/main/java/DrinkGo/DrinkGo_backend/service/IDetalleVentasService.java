package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.DetalleVentas;

public interface IDetalleVentasService {
    List<DetalleVentas> buscarTodos();

    void guardar(DetalleVentas detalleVentas);

    void modificar(DetalleVentas detalleVentas);

    Optional<DetalleVentas> buscarId(Long id);

    void eliminar(Long id);
}
