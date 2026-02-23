package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.UsuariosRoles;
import DrinkGo.DrinkGo_backend.repository.UsuariosRolesRepository;
import DrinkGo.DrinkGo_backend.service.IUsuariosRolesService;

@Service
public class UsuariosRolesService implements IUsuariosRolesService {
    @Autowired
    private UsuariosRolesRepository repoUsuariosRoles;

    public List<UsuariosRoles> buscarTodos() {
        return repoUsuariosRoles.findAll();
    }

    public void guardar(UsuariosRoles usuariosRoles) {
        repoUsuariosRoles.save(usuariosRoles);
    }

    public void modificar(UsuariosRoles usuariosRoles) {
        repoUsuariosRoles.save(usuariosRoles);
    }

    public Optional<UsuariosRoles> buscarId(Long id) {
        return repoUsuariosRoles.findById(id);
    }

    public void eliminar(Long id) {
        repoUsuariosRoles.deleteById(id);
    }
}
