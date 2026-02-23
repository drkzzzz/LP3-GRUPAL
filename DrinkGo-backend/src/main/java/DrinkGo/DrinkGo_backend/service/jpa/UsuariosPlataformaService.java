package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.UsuariosPlataforma;
import DrinkGo.DrinkGo_backend.repository.UsuariosPlataformaRepository;
import DrinkGo.DrinkGo_backend.service.IUsuariosPlataformaService;

@Service
public class UsuariosPlataformaService implements IUsuariosPlataformaService {
    @Autowired
    private UsuariosPlataformaRepository repoUsuariosPlataforma;

    public List<UsuariosPlataforma> buscarTodos() {
        return repoUsuariosPlataforma.findAll();
    }

    public void guardar(UsuariosPlataforma usuariosPlataforma) {
        repoUsuariosPlataforma.save(usuariosPlataforma);
    }

    public void modificar(UsuariosPlataforma usuariosPlataforma) {
        repoUsuariosPlataforma.save(usuariosPlataforma);
    }

    public Optional<UsuariosPlataforma> buscarId(Long id) {
        return repoUsuariosPlataforma.findById(id);
    }

    public void eliminar(Long id) {
        repoUsuariosPlataforma.deleteById(id);
    }
}
