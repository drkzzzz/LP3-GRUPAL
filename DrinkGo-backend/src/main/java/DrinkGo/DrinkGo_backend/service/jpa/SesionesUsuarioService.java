package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.SesionesUsuario;
import DrinkGo.DrinkGo_backend.repository.SesionesUsuarioRepository;
import DrinkGo.DrinkGo_backend.service.ISesionesUsuarioService;

@Service
public class SesionesUsuarioService implements ISesionesUsuarioService {
    @Autowired
    private SesionesUsuarioRepository repoSesionesUsuario;

    public List<SesionesUsuario> buscarTodos() {
        return repoSesionesUsuario.findAll();
    }

    public void guardar(SesionesUsuario sesionesUsuario) {
        repoSesionesUsuario.save(sesionesUsuario);
    }

    public void modificar(SesionesUsuario sesionesUsuario) {
        repoSesionesUsuario.save(sesionesUsuario);
    }

    public Optional<SesionesUsuario> buscarId(Long id) {
        return repoSesionesUsuario.findById(id);
    }

    public void eliminar(Long id) {
        repoSesionesUsuario.deleteById(id);
    }
}
