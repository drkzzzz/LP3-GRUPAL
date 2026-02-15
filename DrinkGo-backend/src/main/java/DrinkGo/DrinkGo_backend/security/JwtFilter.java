package DrinkGo.DrinkGo_backend.security;

import DrinkGo.DrinkGo_backend.entity.Negocio;
import DrinkGo.DrinkGo_backend.entity.SesionUsuario;
import DrinkGo.DrinkGo_backend.entity.Usuario;
import DrinkGo.DrinkGo_backend.repository.NegocioRepository;
import DrinkGo.DrinkGo_backend.repository.SesionUsuarioRepository;
import DrinkGo.DrinkGo_backend.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

/**
 * Filtro JWT que valida cada request protegida.
 * Flujo de validación:
 * 1. Leer token del header Authorization: Bearer <token>
 * 2. Validar firma JWT
 * 3. Generar SHA-256 del token recibido
 * 4. Buscar coincidencia en sesiones_usuario (esta_activo=1, expira_en > NOW)
 * 5. Validar usuario (esta_activo=1, bloqueado_hasta IS NULL)
 * 6. Validar negocio (estado='activo', esta_activo=1, eliminado_en IS NULL)
 * 7. Establecer autenticación en SecurityContextHolder
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SesionUsuarioRepository sesionUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final NegocioRepository negocioRepository;

    public JwtFilter(JwtUtil jwtUtil,
                     SesionUsuarioRepository sesionUsuarioRepository,
                     UsuarioRepository usuarioRepository,
                     NegocioRepository negocioRepository) {
        this.jwtUtil = jwtUtil;
        this.sesionUsuarioRepository = sesionUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.negocioRepository = negocioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // 1. Validar firma del JWT
                if (!jwtUtil.validarToken(token)) {
                    enviarError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido o expirado");
                    return;
                }

                // 2. Generar SHA-256 del token recibido
                String hashToken = JwtUtil.generarHashSHA256(token);

                // 3. Buscar sesión activa en sesiones_usuario
                Optional<SesionUsuario> sesionOpt = sesionUsuarioRepository
                        .findByHashTokenAndEstaActivoTrueAndExpiraEnAfter(hashToken, LocalDateTime.now());

                if (sesionOpt.isEmpty()) {
                    enviarError(response, HttpServletResponse.SC_UNAUTHORIZED, "Sesión no encontrada o expirada");
                    return;
                }

                SesionUsuario sesion = sesionOpt.get();

                // 4. Extraer datos del token
                Long usuarioId = jwtUtil.extraerUsuarioId(token);
                Long negocioId = jwtUtil.extraerNegocioId(token);
                String rol = jwtUtil.extraerRol(token);

                // 5. Validar que el usuario sigue activo
                Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
                if (usuarioOpt.isEmpty()) {
                    enviarError(response, HttpServletResponse.SC_UNAUTHORIZED, "Usuario no encontrado");
                    return;
                }

                Usuario usuario = usuarioOpt.get();
                if (!Boolean.TRUE.equals(usuario.getEstaActivo())) {
                    enviarError(response, HttpServletResponse.SC_FORBIDDEN, "Usuario desactivado");
                    return;
                }
                if (usuario.getBloqueadoHasta() != null && usuario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
                    enviarError(response, HttpServletResponse.SC_FORBIDDEN, "Usuario bloqueado temporalmente");
                    return;
                }
                if (usuario.getEliminadoEn() != null) {
                    enviarError(response, HttpServletResponse.SC_FORBIDDEN, "Usuario eliminado");
                    return;
                }

                // 6. Validar que el negocio sigue activo
                Optional<Negocio> negocioOpt = negocioRepository.findById(negocioId);
                if (negocioOpt.isEmpty()) {
                    enviarError(response, HttpServletResponse.SC_FORBIDDEN, "Negocio no encontrado");
                    return;
                }

                Negocio negocio = negocioOpt.get();
                if (!"activo".equals(negocio.getEstado())) {
                    enviarError(response, HttpServletResponse.SC_FORBIDDEN, "El negocio no está activo (estado: " + negocio.getEstado() + ")");
                    return;
                }
                if (!Boolean.TRUE.equals(negocio.getEstaActivo())) {
                    enviarError(response, HttpServletResponse.SC_FORBIDDEN, "El negocio está desactivado");
                    return;
                }
                if (negocio.getEliminadoEn() != null) {
                    enviarError(response, HttpServletResponse.SC_FORBIDDEN, "El negocio ha sido eliminado");
                    return;
                }

                // 7. Actualizar última actividad de la sesión
                sesion.setUltimaActividadEn(LocalDateTime.now());
                sesionUsuarioRepository.save(sesion);

                // 8. Establecer autenticación en SecurityContextHolder
                UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado(usuarioId, negocioId, rol);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                usuarioAutenticado,
                                null,
                                Collections.emptyList()
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                enviarError(response, HttpServletResponse.SC_UNAUTHORIZED, "Error al procesar token: " + e.getMessage());
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void enviarError(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + mensaje + "\"}");
    }
}
