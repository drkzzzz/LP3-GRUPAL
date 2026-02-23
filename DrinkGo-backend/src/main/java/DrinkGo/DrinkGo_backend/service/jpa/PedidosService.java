package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Pedidos;
import DrinkGo.DrinkGo_backend.repository.PedidosRepository;
import DrinkGo.DrinkGo_backend.service.IPedidosService;

@Service
public class PedidosService implements IPedidosService {
    @Autowired
    private PedidosRepository repoPedidos;

    public List<Pedidos> buscarTodos() {
        return repoPedidos.findAll();
    }

    public void guardar(Pedidos pedidos) {
        repoPedidos.save(pedidos);
    }

    public void modificar(Pedidos pedidos) {
        repoPedidos.save(pedidos);
    }

    public Optional<Pedidos> buscarId(Long id) {
        return repoPedidos.findById(id);
    }

    public void eliminar(Long id) {
        repoPedidos.deleteById(id);
    }
}
