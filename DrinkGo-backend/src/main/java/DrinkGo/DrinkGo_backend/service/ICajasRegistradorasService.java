package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.CajasRegistradoras;

public interface ICajasRegistradorasService {
    List<CajasRegistradoras> buscarTodos();

    void guardar(CajasRegistradoras cajasRegistradoras);

    void modificar(CajasRegistradoras cajasRegistradoras);

    Optional<CajasRegistradoras> buscarId(Long id);

    void eliminar(Long id);
}
