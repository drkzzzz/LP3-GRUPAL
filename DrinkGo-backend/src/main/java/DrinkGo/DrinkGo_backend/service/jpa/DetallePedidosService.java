package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.DetallePedidos;
import DrinkGo.DrinkGo_backend.repository.DetallePedidosRepository;
import DrinkGo.DrinkGo_backend.service.IDetallePedidosService;

@Service
public class DetallePedidosService implements IDetallePedidosService {
    @Autowired
    private DetallePedidosRepository repoDetallePedidos;

    public List<DetallePedidos> buscarTodos() {
        return repoDetallePedidos.findAll();
    }

    public void guardar(DetallePedidos detallePedidos) {
        repoDetallePedidos.save(detallePedidos);
    }

    public void modificar(DetallePedidos detallePedidos) {
        repoDetallePedidos.save(detallePedidos);
    }

    public Optional<DetallePedidos> buscarId(Long id) {
        return repoDetallePedidos.findById(id);
    }

    public void eliminar(Long id) {
        repoDetallePedidos.deleteById(id);
    }
}
