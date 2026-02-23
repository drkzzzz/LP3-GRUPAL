package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Usuarios;
import DrinkGo.DrinkGo_backend.repository.UsuariosRepository;
import DrinkGo.DrinkGo_backend.service.IUsuariosService;

@Service
public class UsuariosService implements IUsuariosService {
    @Autowired
    private UsuariosRepository repoUsuarios;

    public List<Usuarios> buscarTodos() {
        return repoUsuarios.findAll();
    }

    public void guardar(Usuarios usuarios) {
        repoUsuarios.save(usuarios);
    }

    public void modificar(Usuarios usuarios) {
        repoUsuarios.save(usuarios);
    }

    public Optional<Usuarios> buscarId(Long id) {
        return repoUsuarios.findById(id);
    }

    public void eliminar(Long id) {
        repoUsuarios.deleteById(id);
    }
}
