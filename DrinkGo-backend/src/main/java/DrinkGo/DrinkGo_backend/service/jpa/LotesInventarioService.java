package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.LotesInventario;
import DrinkGo.DrinkGo_backend.repository.LotesInventarioRepository;
import DrinkGo.DrinkGo_backend.service.ILotesInventarioService;

@Service
public class LotesInventarioService implements ILotesInventarioService {
    @Autowired
    private LotesInventarioRepository repoLotesInventario;

    public List<LotesInventario> buscarTodos() {
        return repoLotesInventario.findAll();
    }

    public void guardar(LotesInventario lotesInventario) {
        repoLotesInventario.save(lotesInventario);
    }

    public void modificar(LotesInventario lotesInventario) {
        repoLotesInventario.save(lotesInventario);
    }

    public Optional<LotesInventario> buscarId(Long id) {
        return repoLotesInventario.findById(id);
    }

    public void eliminar(Long id) {
        repoLotesInventario.deleteById(id);
    }
}
