package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Combos;
import DrinkGo.DrinkGo_backend.repository.CombosRepository;
import DrinkGo.DrinkGo_backend.service.ICombosService;

@Service
public class CombosService implements ICombosService {
    @Autowired
    private CombosRepository repoCombos;

    public List<Combos> buscarTodos() {
        return repoCombos.findAll();
    }

    public void guardar(Combos combos) {
        repoCombos.save(combos);
    }

    public void modificar(Combos combos) {
        repoCombos.save(combos);
    }

    public Optional<Combos> buscarId(Long id) {
        return repoCombos.findById(id);
    }

    public void eliminar(Long id) {
        repoCombos.deleteById(id);
    }
}
