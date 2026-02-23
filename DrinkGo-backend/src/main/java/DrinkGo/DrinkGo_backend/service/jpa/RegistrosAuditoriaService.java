package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.RegistrosAuditoria;
import DrinkGo.DrinkGo_backend.repository.RegistrosAuditoriaRepository;
import DrinkGo.DrinkGo_backend.service.IRegistrosAuditoriaService;

@Service
public class RegistrosAuditoriaService implements IRegistrosAuditoriaService {
    @Autowired
    private RegistrosAuditoriaRepository repoRegistrosAuditoria;

    public List<RegistrosAuditoria> buscarTodos() {
        return repoRegistrosAuditoria.findAll();
    }

    public void guardar(RegistrosAuditoria registrosAuditoria) {
        repoRegistrosAuditoria.save(registrosAuditoria);
    }

    public void modificar(RegistrosAuditoria registrosAuditoria) {
        repoRegistrosAuditoria.save(registrosAuditoria);
    }

    public Optional<RegistrosAuditoria> buscarId(Long id) {
        return repoRegistrosAuditoria.findById(id);
    }

    public void eliminar(Long id) {
        repoRegistrosAuditoria.deleteById(id);
    }
}
