package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.ConfiguracionTiendaOnline;

public interface IConfiguracionTiendaOnlineService {
    List<ConfiguracionTiendaOnline> buscarTodos();

    void guardar(ConfiguracionTiendaOnline configuracionTiendaOnline);

    void modificar(ConfiguracionTiendaOnline configuracionTiendaOnline);

    Optional<ConfiguracionTiendaOnline> buscarId(Long id);

    void eliminar(Long id);
}
