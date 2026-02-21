package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para crear un nuevo cliente (POST /restful/clientes).
 * Bloque 7.
 */
public class ClienteCreateRequest {

    private String tipoCliente;       // "individual" o "empresa"
    private String nombres;
    private String apellidos;
    private String razonSocial;
    private String tipoDocumento;     // "DNI", "RUC", "CE", "PASAPORTE", "OTRO"
    private String numeroDocumento;
    private String email;
    private String telefono;
    private String direccion;

    // --- Getters y Setters ---

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
