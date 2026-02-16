package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.Notificacion;
import DrinkGo.DrinkGo_backend.repository.NotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional(readOnly = true)
    public List<Notificacion> findByNegocio(Long negocioId) {
        return notificacionRepository.findByNegocioId(negocioId);
    }

    @Transactional(readOnly = true)
    public List<Notificacion> findByUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByCreadoEnDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Notificacion> findByUsuarioNoLeidas(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdAndEstaLeido(usuarioId, false);
    }

    @Transactional(readOnly = true)
    public Notificacion findById(Long id) {
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    @Transactional
    public Notificacion crear(Notificacion notificacion) {
        return notificacionRepository.save(notificacion);
    }

    @Transactional
    public Notificacion marcarComoLeida(Long id) {
        Notificacion notificacion = findById(id);
        notificacion.setEstaLeido(true);
        notificacion.setLeidoEn(LocalDateTime.now());
        return notificacionRepository.save(notificacion);
    }

    @Transactional
    public void marcarTodasComoLeidas(Long usuarioId) {
        List<Notificacion> noLeidas = findByUsuarioNoLeidas(usuarioId);
        noLeidas.forEach(n -> {
            n.setEstaLeido(true);
            n.setLeidoEn(LocalDateTime.now());
        });
        notificacionRepository.saveAll(noLeidas);
    }

    @Transactional
    public void eliminar(Long id) {
        notificacionRepository.deleteById(id);
    }
}
