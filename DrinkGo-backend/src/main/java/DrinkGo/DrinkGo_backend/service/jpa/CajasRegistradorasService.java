package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.CajasRegistradoras;
import DrinkGo.DrinkGo_backend.repository.CajasRegistradorasRepository;
import DrinkGo.DrinkGo_backend.service.ICajasRegistradorasService;

@Service
public class CajasRegistradorasService implements ICajasRegistradorasService {
    @Autowired
    private CajasRegistradorasRepository repoCajasRegistradoras;

    public List<CajasRegistradoras> buscarTodos() {
        return repoCajasRegistradoras.findAll();
    }

    public void guardar(CajasRegistradoras cajasRegistradoras) {
        repoCajasRegistradoras.save(cajasRegistradoras);
    }

    public void modificar(CajasRegistradoras cajasRegistradoras) {
        repoCajasRegistradoras.save(cajasRegistradoras);
    }

    public Optional<CajasRegistradoras> buscarId(Long id) {
        return repoCajasRegistradoras.findById(id);
    }

    public void eliminar(Long id) {
        repoCajasRegistradoras.deleteById(id);
    }
}
