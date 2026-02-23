package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Promociones;
import DrinkGo.DrinkGo_backend.repository.PromocionesRepository;
import DrinkGo.DrinkGo_backend.service.IPromocionesService;

@Service
public class PromocionesService implements IPromocionesService {
    @Autowired
    private PromocionesRepository repoPromociones;

    public List<Promociones> buscarTodos() {
        return repoPromociones.findAll();
    }

    public void guardar(Promociones promociones) {
        repoPromociones.save(promociones);
    }

    public void modificar(Promociones promociones) {
        repoPromociones.save(promociones);
    }

    public Optional<Promociones> buscarId(Long id) {
        return repoPromociones.findById(id);
    }

    public void eliminar(Long id) {
        repoPromociones.deleteById(id);
    }
}
