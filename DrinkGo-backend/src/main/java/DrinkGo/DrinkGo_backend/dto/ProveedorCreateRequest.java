package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para crear un nuevo proveedor (POST /restful/proveedores).
 * Bloque 6.
 */
public class ProveedorCreateRequest {

    private String codigo;
    private String razonSocial;
    private String nombreComercial;
    private String ruc;
    private String direccion;
    private String telefono;
    private String email;
    private String rubro;

    // --- Getters y Setters ---

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRubro() { return rubro; }
    public void setRubro(String rubro) { this.rubro = rubro; }
}
