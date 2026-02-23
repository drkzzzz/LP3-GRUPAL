package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Devoluciones;
import DrinkGo.DrinkGo_backend.repository.DevolucionesRepository;
import DrinkGo.DrinkGo_backend.service.IDevolucionesService;

@Service
public class DevolucionesService implements IDevolucionesService {
    @Autowired
    private DevolucionesRepository repoDevoluciones;

    public List<Devoluciones> buscarTodos() {
        return repoDevoluciones.findAll();
    }

    public void guardar(Devoluciones devoluciones) {
        repoDevoluciones.save(devoluciones);
    }

    public void modificar(Devoluciones devoluciones) {
        repoDevoluciones.save(devoluciones);
    }

    public Optional<Devoluciones> buscarId(Long id) {
        return repoDevoluciones.findById(id);
    }

    public void eliminar(Long id) {
        repoDevoluciones.deleteById(id);
    }
}
