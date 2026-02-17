package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

/**
 * DTO para actualizar una dirección de cliente (PUT /restful/clientes/{clienteId}/direcciones/{id}).
 * Bloque 7. Todos los campos son opcionales.
 */
public class DireccionClienteUpdateRequest {

    private String etiqueta;
    private String direccion;
    private String direccion2;
    private String ciudad;
    private String departamento;
    private String pais;
    private String codigoPostal;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String referencia;
    private String telefonoContacto;
    private Boolean esPredeterminado;

    // --- Getters y Setters ---

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getDireccion2() { return direccion2; }
    public void setDireccion2(String direccion2) { this.direccion2 = direccion2; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public BigDecimal getLatitud() { return latitud; }
    public void setLatitud(BigDecimal latitud) { this.latitud = latitud; }

    public BigDecimal getLongitud() { return longitud; }
    public void setLongitud(BigDecimal longitud) { this.longitud = longitud; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }

    public Boolean getEsPredeterminado() { return esPredeterminado; }
    public void setEsPredeterminado(Boolean esPredeterminado) { this.esPredeterminado = esPredeterminado; }
}