package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.UsuarioSede;
import DrinkGo.DrinkGo_backend.repository.UsuarioSedeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioSedeService {

    private final UsuarioSedeRepository usuarioSedeRepository;

    public UsuarioSedeService(UsuarioSedeRepository usuarioSedeRepository) {
        this.usuarioSedeRepository = usuarioSedeRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioSede> findByUsuario(Long usuarioId) {
        return usuarioSedeRepository.findByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<UsuarioSede> findBySede(Long sedeId) {
        return usuarioSedeRepository.findBySedeId(sedeId);
    }

    @Transactional(readOnly = true)
    public UsuarioSede findById(Long id) {
        return usuarioSedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación usuario-sede no encontrada"));
    }

    @Transactional
    public UsuarioSede crear(UsuarioSede usuarioSede) {
        if (usuarioSedeRepository.findByUsuarioIdAndSedeId(usuarioSede.getUsuarioId(), usuarioSede.getSedeId())
                .isPresent()) {
            throw new RuntimeException("El usuario ya está asignado a esta sede");
        }
        return usuarioSedeRepository.save(usuarioSede);
    }

    @Transactional
    public void eliminar(Long id) {
        usuarioSedeRepository.deleteById(id);
    }

    @Transactional
    public UsuarioSede establecerPredeterminada(Long usuarioId, Long sedeId) {
        // Primero quitar el flag de todas las sedes del usuario
        List<UsuarioSede> asignaciones = findByUsuario(usuarioId);
        asignaciones.forEach(a -> {
            a.setEsPredeterminado(false);
            usuarioSedeRepository.save(a);
        });

        // Luego establecer la nueva como predeterminada
        UsuarioSede asignacion = usuarioSedeRepository.findByUsuarioIdAndSedeId(usuarioId, sedeId)
                .orElseThrow(() -> new RuntimeException("Usuario no asignado a esta sede"));
        asignacion.setEsPredeterminado(true);
        return usuarioSedeRepository.save(asignacion);
    }
}
