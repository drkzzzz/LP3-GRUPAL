package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Negocios;
import DrinkGo.DrinkGo_backend.repository.NegociosRepository;
import DrinkGo.DrinkGo_backend.service.INegociosService;

@Service
public class NegociosService implements INegociosService {
    @Autowired
    private NegociosRepository repoNegocios;

    public List<Negocios> buscarTodos() {
        return repoNegocios.findAll();
    }

    public void guardar(Negocios negocios) {
        repoNegocios.save(negocios);
    }

    public void modificar(Negocios negocios) {
        repoNegocios.save(negocios);
    }

    public Optional<Negocios> buscarId(Long id) {
        return repoNegocios.findById(id);
    }

    public void eliminar(Long id) {
        repoNegocios.deleteById(id);
    }
}
