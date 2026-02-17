package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.RegistroAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para RegistroAuditoria
 */
@Repository
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {

    List<RegistroAuditoria> findByNegocioId(Long negocioId);

    List<RegistroAuditoria> findByUsuarioId(Long usuarioId);

    List<RegistroAuditoria> findByAccion(String accion);

    List<RegistroAuditoria> findByTipoEntidad(String tipoEntidad);

    List<RegistroAuditoria> findByTipoEntidadAndEntidadId(String tipoEntidad, Long entidadId);

    List<RegistroAuditoria> findByNegocioIdAndTipoEntidad(Long negocioId, String tipoEntidad);

    List<RegistroAuditoria> findByNegocioIdAndUsuarioId(Long negocioId, Long usuarioId);

    List<RegistroAuditoria> findByFechaAccionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<RegistroAuditoria> findByNegocioIdAndFechaAccionBetween(Long negocioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
