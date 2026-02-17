package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDate;

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
    private String telefonoSecundario;
    private LocalDate fechaNacimiento;
    private String genero;            // "M", "F", "OTRO", "NO_ESPECIFICADO"
    private Boolean aceptaMarketing;
    private String canalMarketing;
    private String notas;

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

    public String getTelefonoSecundario() { return telefonoSecundario; }
    public void setTelefonoSecundario(String telefonoSecundario) { this.telefonoSecundario = telefonoSecundario; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Boolean getAceptaMarketing() { return aceptaMarketing; }
    public void setAceptaMarketing(Boolean aceptaMarketing) { this.aceptaMarketing = aceptaMarketing; }

    public String getCanalMarketing() { return canalMarketing; }
    public void setCanalMarketing(String canalMarketing) { this.canalMarketing = canalMarketing; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}