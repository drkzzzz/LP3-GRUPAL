package DrinkGo.DrinkGo_backend.dto;

import java.util.Set;

public class UsuarioDTO {
    private String codigoEmpleado;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private String contrasena;
    private Set<String> rolesCodigos;
    private Long sedePreferidaId;

    public String getCodigoEmpleado() { return codigoEmpleado; }
    public void setCodigoEmpleado(String codigoEmpleado) { this.codigoEmpleado = codigoEmpleado; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Set<String> getRolesCodigos() { return rolesCodigos; }
    public void setRolesCodigos(Set<String> rolesCodigos) { this.rolesCodigos = rolesCodigos; }

    public Long getSedePreferidaId() { return sedePreferidaId; }
    public void setSedePreferidaId(Long sedePreferidaId) { this.sedePreferidaId = sedePreferidaId; }
}
