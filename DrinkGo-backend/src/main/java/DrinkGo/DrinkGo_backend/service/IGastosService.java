package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.Gastos;
import org.springframework.web.multipart.MultipartFile;

public interface IGastosService {
    List<Gastos> buscarTodos();

    List<Gastos> buscarPorNegocio(Long negocioId);

    Gastos guardar(Gastos gastos);

    Gastos modificar(Gastos gastos);

    Optional<Gastos> buscarId(Long id);

    void eliminar(Long id);

    Gastos marcarPagado(Long id, String metodoPago, String referencia);

    Gastos subirComprobante(Long id, MultipartFile archivo, String metodoPago, String referenciaPago);

    Gastos eliminarComprobante(Long id);
}
