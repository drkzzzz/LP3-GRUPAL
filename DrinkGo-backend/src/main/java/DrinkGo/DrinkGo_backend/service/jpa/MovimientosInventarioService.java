package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.MovimientosInventario;
import DrinkGo.DrinkGo_backend.repository.MovimientosInventarioRepository;
import DrinkGo.DrinkGo_backend.service.IMovimientosInventarioService;

@Service
public class MovimientosInventarioService implements IMovimientosInventarioService {
    @Autowired
    private MovimientosInventarioRepository repoMovimientosInventario;

    public List<MovimientosInventario> buscarTodos() {
        return repoMovimientosInventario.findAll();
    }

    public void guardar(MovimientosInventario movimientosInventario) {
        repoMovimientosInventario.save(movimientosInventario);
    }

    public void modificar(MovimientosInventario movimientosInventario) {
        repoMovimientosInventario.save(movimientosInventario);
    }

    public Optional<MovimientosInventario> buscarId(Long id) {
        return repoMovimientosInventario.findById(id);
    }

    public void eliminar(Long id) {
        repoMovimientosInventario.deleteById(id);
    }
}
