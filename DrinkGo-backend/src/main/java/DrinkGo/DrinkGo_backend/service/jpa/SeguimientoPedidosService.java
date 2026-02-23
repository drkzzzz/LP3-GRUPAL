package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.SeguimientoPedidos;
import DrinkGo.DrinkGo_backend.repository.SeguimientoPedidosRepository;
import DrinkGo.DrinkGo_backend.service.ISeguimientoPedidosService;

@Service
public class SeguimientoPedidosService implements ISeguimientoPedidosService {
    @Autowired
    private SeguimientoPedidosRepository repoSeguimientoPedidos;

    public List<SeguimientoPedidos> buscarTodos() {
        return repoSeguimientoPedidos.findAll();
    }

    public void guardar(SeguimientoPedidos seguimientoPedidos) {
        repoSeguimientoPedidos.save(seguimientoPedidos);
    }

    public void modificar(SeguimientoPedidos seguimientoPedidos) {
        repoSeguimientoPedidos.save(seguimientoPedidos);
    }

    public Optional<SeguimientoPedidos> buscarId(Long id) {
        return repoSeguimientoPedidos.findById(id);
    }

    public void eliminar(Long id) {
        repoSeguimientoPedidos.deleteById(id);
    }
}
