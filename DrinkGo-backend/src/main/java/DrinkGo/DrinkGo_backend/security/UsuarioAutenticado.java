package DrinkGo.DrinkGo_backend.security;

/**
 * Objeto que representa al usuario autenticado en el SecurityContext.
 * Contiene el ID del usuario, ID del negocio (tenant) y el rol.
 * Se almacena como principal en SecurityContextHolder.
 */
public class UsuarioAutenticado {

    private final Long usuarioId;
    private final Long negocioId;
    private final String rol;

    public UsuarioAutenticado(Long usuarioId, Long negocioId, String rol) {
        this.usuarioId = usuarioId;
        this.negocioId = negocioId;
        this.rol = rol;
    }

    public Long getUsuarioId() { return usuarioId; }
    public Long getNegocioId() { return negocioId; }
    public String getRol() { return rol; }

    @Override
    public String toString() {
        return "UsuarioAutenticado{usuarioId=" + usuarioId + ", negocioId=" + negocioId + ", rol='" + rol + "'}";
    }
}
