package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.StockInventario;
import DrinkGo.DrinkGo_backend.repository.StockInventarioRepository;
import DrinkGo.DrinkGo_backend.service.IStockInventarioService;

@Service
public class StockInventarioService implements IStockInventarioService {
    @Autowired
    private StockInventarioRepository repoStockInventario;

    public List<StockInventario> buscarTodos() {
        return repoStockInventario.findAll();
    }

    public void guardar(StockInventario stockInventario) {
        repoStockInventario.save(stockInventario);
    }

    public void modificar(StockInventario stockInventario) {
        repoStockInventario.save(stockInventario);
    }

    public Optional<StockInventario> buscarId(Long id) {
        return repoStockInventario.findById(id);
    }

    public void eliminar(Long id) {
        repoStockInventario.deleteById(id);
    }
}
