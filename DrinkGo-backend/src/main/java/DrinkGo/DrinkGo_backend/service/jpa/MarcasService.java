package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Marcas;
import DrinkGo.DrinkGo_backend.repository.MarcasRepository;
import DrinkGo.DrinkGo_backend.service.IMarcasService;

@Service
public class MarcasService implements IMarcasService {
    @Autowired
    private MarcasRepository repoMarcas;

    public List<Marcas> buscarTodos() {
        return repoMarcas.findAll();
    }

    public void guardar(Marcas marcas) {
        repoMarcas.save(marcas);
    }

    public void modificar(Marcas marcas) {
        repoMarcas.save(marcas);
    }

    public Optional<Marcas> buscarId(Long id) {
        return repoMarcas.findById(id);
    }

    public void eliminar(Long id) {
        repoMarcas.deleteById(id);
    }
}
