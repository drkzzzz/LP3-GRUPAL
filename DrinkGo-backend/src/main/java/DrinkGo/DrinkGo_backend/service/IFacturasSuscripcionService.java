package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.FacturasSuscripcion;

public interface IFacturasSuscripcionService {
    List<FacturasSuscripcion> buscarTodos();

    List<FacturasSuscripcion> buscarPorNegocio(Long negocioId);

    void guardar(FacturasSuscripcion facturasSuscripcion);

    void modificar(FacturasSuscripcion facturasSuscripcion);

    Optional<FacturasSuscripcion> buscarId(Long id);

    void eliminar(Long id);

    FacturasSuscripcion generarFactura(Long suscripcionId);

    FacturasSuscripcion marcarPagada(Long id, String metodoPago, String referenciaPago);

    FacturasSuscripcion cancelarFactura(Long id);
}
