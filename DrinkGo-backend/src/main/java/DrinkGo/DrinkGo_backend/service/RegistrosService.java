package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Registros;
import DrinkGo.DrinkGo_backend.repository.RegistrosRepository;

@Service
public class RegistrosService implements IRegistrosService {
    @Autowired
    private RegistrosRepository repoRegistros;

    public List<Registros> BuscarTodos() {
        return repoRegistros.findAll();
    }

    public void guardar(Registros registro) {
        repoRegistros.save(registro);
    }

    public void modificar(Registros registro) {
        repoRegistros.save(registro);
    }

    public Optional<Registros> BuscarId(Integer id) {
        return repoRegistros.findById(id);
    }

    public void eliminar(Integer id) {
        repoRegistros.deleteById(id);
    }

}
