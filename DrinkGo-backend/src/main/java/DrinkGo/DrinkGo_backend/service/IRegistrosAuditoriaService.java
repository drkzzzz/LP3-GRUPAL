package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.RegistrosAuditoria;

public interface IRegistrosAuditoriaService {
    List<RegistrosAuditoria> buscarTodos();
    void guardar(RegistrosAuditoria registrosAuditoria);
    void modificar(RegistrosAuditoria registrosAuditoria);
    Optional<RegistrosAuditoria> buscarId(Long id);
    void eliminar(Long id);
}
