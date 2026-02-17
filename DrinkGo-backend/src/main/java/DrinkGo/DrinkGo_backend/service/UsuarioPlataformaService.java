package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.UsuarioPlataformaRequest;
import DrinkGo.DrinkGo_backend.dto.UsuarioPlataformaResponse;
import DrinkGo.DrinkGo_backend.entity.UsuarioPlataforma;
import DrinkGo.DrinkGo_backend.repository.UsuarioPlataformaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para UsuarioPlataforma
 */
@Service
public class UsuarioPlataformaService {

    @Autowired
    private UsuarioPlataformaRepository usuarioPlataformaRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional(readOnly = true)
    public List<UsuarioPlataformaResponse> obtenerTodosLosUsuarios() {
        return usuarioPlataformaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioPlataformaResponse> obtenerUsuariosActivos() {
        return usuarioPlataformaRepository.findByEstaActivoTrue().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioPlataformaResponse obtenerUsuarioPorId(Long id) {
        UsuarioPlataforma usuario = usuarioPlataformaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario de plataforma no encontrado con ID: " + id));
        return convertirAResponse(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioPlataformaResponse obtenerUsuarioPorEmail(String email) {
        UsuarioPlataforma usuario = usuarioPlataformaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario de plataforma no encontrado con email: " + email));
        return convertirAResponse(usuario);
    }

    @Transactional
    public UsuarioPlataformaResponse crearUsuarioPlataforma(UsuarioPlataformaRequest request) {
        if (usuarioPlataformaRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario de plataforma con el email: " + request.getEmail());
        }

        UsuarioPlataforma usuario = new UsuarioPlataforma();
        mapearRequestAEntidad(request, usuario, true);
        UsuarioPlataforma guardado = usuarioPlataformaRepository.save(usuario);
        return convertirAResponse(guardado);
    }

    @Transactional
    public UsuarioPlataformaResponse actualizarUsuarioPlataforma(Long id, UsuarioPlataformaRequest request) {
        UsuarioPlataforma usuario = usuarioPlataformaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario de plataforma no encontrado con ID: " + id));

        // No permitir cambio de email a uno existente
        if (!usuario.getEmail().equals(request.getEmail()) 
            && usuarioPlataformaRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario de plataforma con el email: " + request.getEmail());
        }

        mapearRequestAEntidad(request, usuario, request.getContrasena() != null);
        UsuarioPlataforma actualizado = usuarioPlataformaRepository.save(usuario);
        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminarUsuarioPlataforma(Long id) {
        if (!usuarioPlataformaRepository.existsById(id)) {
            throw new RuntimeException("Usuario de plataforma no encontrado con ID: " + id);
        }
        usuarioPlataformaRepository.deleteById(id);
    }

    @Transactional
    public void registrarAcceso(Long id, String ipAddress) {
        UsuarioPlataforma usuario = usuarioPlataformaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setUltimoAccesoEn(LocalDateTime.now());
        usuario.setIpUltimoAcceso(ipAddress);
        usuario.setIntentosFallidosAcceso(0);
        usuarioPlataformaRepository.save(usuario);
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(UsuarioPlataformaRequest request, UsuarioPlataforma usuario, boolean actualizarContrasena) {
        usuario.setEmail(request.getEmail());
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setTelefono(request.getTelefono());
        usuario.setUrlAvatar(request.getUrlAvatar());
        
        if (request.getRol() != null) {
            usuario.setRol(UsuarioPlataforma.RolPlataforma.valueOf(request.getRol()));
        }
        
        if (request.getEstaActivo() != null) {
            usuario.setEstaActivo(request.getEstaActivo());
        }
        
        if (actualizarContrasena && request.getContrasena() != null) {
            usuario.setHashContrasena(passwordEncoder.encode(request.getContrasena()));
            usuario.setContrasenaCambiadaEn(LocalDateTime.now());
        }
    }

    private UsuarioPlataformaResponse convertirAResponse(UsuarioPlataforma usuario) {
        UsuarioPlataformaResponse response = new UsuarioPlataformaResponse();
        response.setId(usuario.getId());
        response.setUuid(usuario.getUuid());
        response.setEmail(usuario.getEmail());
        response.setNombres(usuario.getNombres());
        response.setApellidos(usuario.getApellidos());
        response.setTelefono(usuario.getTelefono());
        response.setUrlAvatar(usuario.getUrlAvatar());
        response.setRol(usuario.getRol() != null ? usuario.getRol().name() : null);
        response.setEstaActivo(usuario.getEstaActivo());
        response.setUltimoAccesoEn(usuario.getUltimoAccesoEn());
        response.setIpUltimoAcceso(usuario.getIpUltimoAcceso());
        response.setContrasenaCambiadaEn(usuario.getContrasenaCambiadaEn());
        response.setIntentosFallidosAcceso(usuario.getIntentosFallidosAcceso());
        response.setBloqueadoHasta(usuario.getBloqueadoHasta());
        response.setCreadoEn(usuario.getCreadoEn());
        response.setActualizadoEn(usuario.getActualizadoEn());
        return response;
    }
}
