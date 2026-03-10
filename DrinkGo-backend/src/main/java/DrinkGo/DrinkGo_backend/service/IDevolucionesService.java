package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;

import DrinkGo.DrinkGo_backend.entity.Devoluciones;

public interface IDevolucionesService {
    List<Devoluciones> buscarTodos();
    List<Devoluciones> buscarPorNegocio(Long negocioId);
    List<Devoluciones> buscarPorVenta(Long ventaId);
    void guardar(Devoluciones devoluciones);
    void modificar(Devoluciones devoluciones);
    Optional<Devoluciones> buscarId(Long id);
    void eliminar(Long id);
    Devoluciones aprobar(Long devolucionId, Long usuarioId);
    Devoluciones rechazar(Long devolucionId, Long usuarioId, String razon);
    String generarNumeroDevolucion(Long negocioId);
}
