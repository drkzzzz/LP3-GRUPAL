package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.RolesPermisos;
import DrinkGo.DrinkGo_backend.repository.RolesPermisosRepository;
import DrinkGo.DrinkGo_backend.service.IRolesPermisosService;

@Service
public class RolesPermisosService implements IRolesPermisosService {
    @Autowired
    private RolesPermisosRepository repoRolesPermisos;

    public List<RolesPermisos> buscarTodos() {
        return repoRolesPermisos.findAll();
    }

    public void guardar(RolesPermisos rolesPermisos) {
        repoRolesPermisos.save(rolesPermisos);
    }

    public void modificar(RolesPermisos rolesPermisos) {
        repoRolesPermisos.save(rolesPermisos);
    }

    public Optional<RolesPermisos> buscarId(Long id) {
        return repoRolesPermisos.findById(id);
    }

    public void eliminar(Long id) {
        repoRolesPermisos.deleteById(id);
    }
}
