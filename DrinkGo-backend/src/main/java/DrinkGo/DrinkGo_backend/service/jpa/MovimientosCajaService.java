package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.MovimientosCaja;
import DrinkGo.DrinkGo_backend.repository.MovimientosCajaRepository;
import DrinkGo.DrinkGo_backend.service.IMovimientosCajaService;

@Service
public class MovimientosCajaService implements IMovimientosCajaService {
    @Autowired
    private MovimientosCajaRepository repoMovimientosCaja;

    public List<MovimientosCaja> buscarTodos() {
        return repoMovimientosCaja.findAll();
    }

    public void guardar(MovimientosCaja movimientosCaja) {
        repoMovimientosCaja.save(movimientosCaja);
    }

    public void modificar(MovimientosCaja movimientosCaja) {
        repoMovimientosCaja.save(movimientosCaja);
    }

    public Optional<MovimientosCaja> buscarId(Long id) {
        return repoMovimientosCaja.findById(id);
    }

    public void eliminar(Long id) {
        repoMovimientosCaja.deleteById(id);
    }
}
