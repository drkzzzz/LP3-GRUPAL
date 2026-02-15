package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para respuesta de login exitoso.
 * Contiene el token JWT y datos básicos del usuario autenticado.
 */
public class LoginResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long usuarioId;
    private Long negocioId;
    private String nombreUsuario;
    private String rol;
    private String mensaje;

    public LoginResponse() {}

    public LoginResponse(String accessToken, Long usuarioId, Long negocioId, String nombreUsuario, String rol) {
        this.accessToken = accessToken;
        this.usuarioId = usuarioId;
        this.negocioId = negocioId;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
        this.mensaje = "Autenticación exitosa";
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
