package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.CondicionesPromocion;
import DrinkGo.DrinkGo_backend.repository.CondicionesPromocionRepository;
import DrinkGo.DrinkGo_backend.service.ICondicionesPromocionService;

@Service
public class CondicionesPromocionService implements ICondicionesPromocionService {
    @Autowired
    private CondicionesPromocionRepository repoCondicionesPromocion;

    public List<CondicionesPromocion> buscarTodos() {
        return repoCondicionesPromocion.findAll();
    }

    public void guardar(CondicionesPromocion condicionesPromocion) {
        repoCondicionesPromocion.save(condicionesPromocion);
    }

    public void modificar(CondicionesPromocion condicionesPromocion) {
        repoCondicionesPromocion.save(condicionesPromocion);
    }

    public Optional<CondicionesPromocion> buscarId(Long id) {
        return repoCondicionesPromocion.findById(id);
    }

    public void eliminar(Long id) {
        repoCondicionesPromocion.deleteById(id);
    }
}
