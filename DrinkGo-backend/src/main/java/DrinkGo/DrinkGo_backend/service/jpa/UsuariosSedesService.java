package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.UsuariosSedes;
import DrinkGo.DrinkGo_backend.repository.UsuariosSedesRepository;
import DrinkGo.DrinkGo_backend.service.IUsuariosSedesService;

@Service
public class UsuariosSedesService implements IUsuariosSedesService {
    @Autowired
    private UsuariosSedesRepository repoUsuariosSedes;

    public List<UsuariosSedes> buscarTodos() {
        return repoUsuariosSedes.findAll();
    }

    public void guardar(UsuariosSedes usuariosSedes) {
        repoUsuariosSedes.save(usuariosSedes);
    }

    public void modificar(UsuariosSedes usuariosSedes) {
        repoUsuariosSedes.save(usuariosSedes);
    }

    public Optional<UsuariosSedes> buscarId(Long id) {
        return repoUsuariosSedes.findById(id);
    }

    public void eliminar(Long id) {
        repoUsuariosSedes.deleteById(id);
    }
}
