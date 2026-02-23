package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Suscripciones;
import DrinkGo.DrinkGo_backend.repository.SuscripcionesRepository;
import DrinkGo.DrinkGo_backend.service.ISuscripcionesService;

@Service
public class SuscripcionesService implements ISuscripcionesService {
    @Autowired
    private SuscripcionesRepository repoSuscripciones;

    public List<Suscripciones> buscarTodos() {
        return repoSuscripciones.findAll();
    }

    public void guardar(Suscripciones suscripciones) {
        repoSuscripciones.save(suscripciones);
    }

    public void modificar(Suscripciones suscripciones) {
        repoSuscripciones.save(suscripciones);
    }

    public Optional<Suscripciones> buscarId(Long id) {
        return repoSuscripciones.findById(id);
    }

    public void eliminar(Long id) {
        repoSuscripciones.deleteById(id);
    }
}
