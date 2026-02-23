package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.DetalleCombos;
import DrinkGo.DrinkGo_backend.repository.DetalleCombosRepository;
import DrinkGo.DrinkGo_backend.service.IDetalleCombosService;

@Service
public class DetalleCombosService implements IDetalleCombosService {
    @Autowired
    private DetalleCombosRepository repoDetalleCombos;

    public List<DetalleCombos> buscarTodos() {
        return repoDetalleCombos.findAll();
    }

    public void guardar(DetalleCombos detalleCombos) {
        repoDetalleCombos.save(detalleCombos);
    }

    public void modificar(DetalleCombos detalleCombos) {
        repoDetalleCombos.save(detalleCombos);
    }

    public Optional<DetalleCombos> buscarId(Long id) {
        return repoDetalleCombos.findById(id);
    }

    public void eliminar(Long id) {
        repoDetalleCombos.deleteById(id);
    }
}
