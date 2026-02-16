package DrinkGo.DrinkGo_backend.security;

import DrinkGo.DrinkGo_backend.entity.SesionUsuario;
import DrinkGo.DrinkGo_backend.entity.Usuario;
import DrinkGo.DrinkGo_backend.repository.SesionUsuarioRepository;
import DrinkGo.DrinkGo_backend.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * Filtro JWT basado en CLASE_API_REFERENCIA.md
 * Valida el token enviado en el header Authorization: Bearer <token>
 * Busca el token en 'sesiones_usuario' (equivalente a buscar en 'registros').
 * Extrae el UUID del usuario y lo guarda en SecurityContextHolder.
 */
@Component
public class JwtFilter extends GenericFilter {

    @Autowired
    private SesionUsuarioRepository sesionUsuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // Buscar el token en sesiones_usuario (equivalente al patrón de clase)
            Optional<SesionUsuario> match = sesionUsuarioRepository
                    .findByHashTokenAndEstaActivo(token, true);

            if (match.isPresent()) {
                SesionUsuario sesion = match.get();

                // Obtener UUID del usuario (equivalente a cliente_id)
                Optional<Usuario> usuario = usuarioRepository.findById(sesion.getUsuarioId());

                if (usuario.isPresent()) {
                    String uuid = usuario.get().getUuid();

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    uuid,
                                    null,
                                    Collections.emptyList()
                            );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        chain.doFilter(req, res);
    }
}
