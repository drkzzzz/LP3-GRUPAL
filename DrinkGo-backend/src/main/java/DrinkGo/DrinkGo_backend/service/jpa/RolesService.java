package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Roles;
import DrinkGo.DrinkGo_backend.repository.RolesRepository;
import DrinkGo.DrinkGo_backend.service.IRolesService;

@Service
public class RolesService implements IRolesService {
    @Autowired
    private RolesRepository repoRoles;

    public List<Roles> buscarTodos() {
        return repoRoles.findAll();
    }

    public void guardar(Roles roles) {
        repoRoles.save(roles);
    }

    public void modificar(Roles roles) {
        repoRoles.save(roles);
    }

    public Optional<Roles> buscarId(Long id) {
        return repoRoles.findById(id);
    }

    public void eliminar(Long id) {
        repoRoles.deleteById(id);
    }
}
