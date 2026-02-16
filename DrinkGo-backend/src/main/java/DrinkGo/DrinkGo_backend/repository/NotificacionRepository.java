package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByNegocioId(Long negocioId);

    List<Notificacion> findByUsuarioId(Long usuarioId);

    List<Notificacion> findByUsuarioIdAndEstaLeido(Long usuarioId, Boolean estaLeido);

    List<Notificacion> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);

    List<Notificacion> findByUsuarioPlataformaId(Long usuarioPlataformaId);

    List<Notificacion> findByEstadoEntrega(String estadoEntrega);

    List<Notificacion> findByPrioridad(String prioridad);
}
