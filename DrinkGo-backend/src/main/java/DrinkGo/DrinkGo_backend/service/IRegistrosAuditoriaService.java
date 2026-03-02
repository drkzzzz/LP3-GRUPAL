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

    /** Atajo para registrar un evento sin JPA boilerplate. */
    default void registrar(String accion, String tipoEntidad, Long entidadId,
            String descripcion, RegistrosAuditoria.Severidad severidad,
            String usuario, Long negocioId) {
        RegistrosAuditoria r = new RegistrosAuditoria();
        r.setAccion(accion);
        r.setTipoEntidad(tipoEntidad);
        r.setEntidadId(entidadId);
        r.setDescripcion(descripcion);
        r.setSeveridad(severidad);
        r.setUsuario(usuario);
        r.setNegocioId(negocioId);
        guardar(r);
    }
}
