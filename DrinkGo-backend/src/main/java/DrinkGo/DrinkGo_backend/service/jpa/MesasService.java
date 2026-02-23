package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Mesas;
import DrinkGo.DrinkGo_backend.repository.MesasRepository;
import DrinkGo.DrinkGo_backend.service.IMesasService;

@Service
public class MesasService implements IMesasService {
    @Autowired
    private MesasRepository repoMesas;

    public List<Mesas> buscarTodos() {
        return repoMesas.findAll();
    }

    public void guardar(Mesas mesas) {
        repoMesas.save(mesas);
    }

    public void modificar(Mesas mesas) {
        repoMesas.save(mesas);
    }

    public Optional<Mesas> buscarId(Long id) {
        return repoMesas.findById(id);
    }

    public void eliminar(Long id) {
        repoMesas.deleteById(id);
    }
}
