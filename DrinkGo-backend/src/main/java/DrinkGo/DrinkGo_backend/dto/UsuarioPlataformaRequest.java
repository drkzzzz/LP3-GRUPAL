package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO Request para UsuarioPlataforma
 */
public class UsuarioPlataformaRequest {

    private String email;
    private String contrasena;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String urlAvatar;
    private String rol; // superadmin, soporte_plataforma, visualizador_plataforma
    private Boolean estaActivo;

    // ── Getters y Setters ──

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getUrlAvatar() { return urlAvatar; }
    public void setUrlAvatar(String urlAvatar) { this.urlAvatar = urlAvatar; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }
}
