package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.ConfiguracionTiendaOnline;
import DrinkGo.DrinkGo_backend.repository.ConfiguracionTiendaOnlineRepository;
import DrinkGo.DrinkGo_backend.service.IConfiguracionTiendaOnlineService;

@Service
public class ConfiguracionTiendaOnlineService implements IConfiguracionTiendaOnlineService {
    @Autowired
    private ConfiguracionTiendaOnlineRepository repoConfiguracionTiendaOnline;

    public List<ConfiguracionTiendaOnline> buscarTodos() {
        return repoConfiguracionTiendaOnline.findAll();
    }

    public void guardar(ConfiguracionTiendaOnline configuracionTiendaOnline) {
        repoConfiguracionTiendaOnline.save(configuracionTiendaOnline);
    }

    public void modificar(ConfiguracionTiendaOnline configuracionTiendaOnline) {
        repoConfiguracionTiendaOnline.save(configuracionTiendaOnline);
    }

    public Optional<ConfiguracionTiendaOnline> buscarId(Long id) {
        return repoConfiguracionTiendaOnline.findById(id);
    }

    public void eliminar(Long id) {
        repoConfiguracionTiendaOnline.deleteById(id);
    }
}
