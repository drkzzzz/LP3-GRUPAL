package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Usuarios;
import DrinkGo.DrinkGo_backend.repository.UsuariosRepository;
import DrinkGo.DrinkGo_backend.service.IUsuariosService;

@Service
public class UsuariosService implements IUsuariosService {
    @Autowired
    private UsuariosRepository repoUsuarios;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /** Hash the password only if it is NOT already a BCrypt hash. */
    private String maybeHash(String raw) {
        if (raw == null || raw.startsWith("$2"))
            return raw;
        return passwordEncoder.encode(raw);
    }

    public List<Usuarios> buscarTodos() {
        return repoUsuarios.findAll();
    }

    public List<Usuarios> buscarPorNegocio(Long negocioId) {
        return repoUsuarios.findByNegocio_Id(negocioId);
    }

    public void guardar(Usuarios usuarios) {
        usuarios.setHashContrasena(maybeHash(usuarios.getHashContrasena()));
        repoUsuarios.save(usuarios);
    }

    public void modificar(Usuarios usuarios) {
        usuarios.setHashContrasena(maybeHash(usuarios.getHashContrasena()));
        repoUsuarios.save(usuarios);
    }

    public Optional<Usuarios> buscarId(Long id) {
        return repoUsuarios.findById(id);
    }

    public void eliminar(Long id) {
        repoUsuarios.deleteById(id);
    }
}
