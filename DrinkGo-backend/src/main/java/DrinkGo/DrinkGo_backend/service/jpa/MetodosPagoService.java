package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.MetodosPago;
import DrinkGo.DrinkGo_backend.repository.MetodosPagoRepository;
import DrinkGo.DrinkGo_backend.service.IMetodosPagoService;

@Service
public class MetodosPagoService implements IMetodosPagoService {
    @Autowired
    private MetodosPagoRepository repoMetodosPago;

    public List<MetodosPago> buscarTodos() {
        return repoMetodosPago.findAll();
    }

    public void guardar(MetodosPago metodosPago) {
        repoMetodosPago.save(metodosPago);
    }

    public void modificar(MetodosPago metodosPago) {
        repoMetodosPago.save(metodosPago);
    }

    public Optional<MetodosPago> buscarId(Long id) {
        return repoMetodosPago.findById(id);
    }

    public void eliminar(Long id) {
        repoMetodosPago.deleteById(id);
    }
}
