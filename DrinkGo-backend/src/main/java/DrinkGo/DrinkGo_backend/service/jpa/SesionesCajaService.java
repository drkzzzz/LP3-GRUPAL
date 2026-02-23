package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.SesionesCaja;
import DrinkGo.DrinkGo_backend.repository.SesionesCajaRepository;
import DrinkGo.DrinkGo_backend.service.ISesionesCajaService;

@Service
public class SesionesCajaService implements ISesionesCajaService {
    @Autowired
    private SesionesCajaRepository repoSesionesCaja;

    public List<SesionesCaja> buscarTodos() {
        return repoSesionesCaja.findAll();
    }

    public void guardar(SesionesCaja sesionesCaja) {
        repoSesionesCaja.save(sesionesCaja);
    }

    public void modificar(SesionesCaja sesionesCaja) {
        repoSesionesCaja.save(sesionesCaja);
    }

    public Optional<SesionesCaja> buscarId(Long id) {
        return repoSesionesCaja.findById(id);
    }

    public void eliminar(Long id) {
        repoSesionesCaja.deleteById(id);
    }
}
