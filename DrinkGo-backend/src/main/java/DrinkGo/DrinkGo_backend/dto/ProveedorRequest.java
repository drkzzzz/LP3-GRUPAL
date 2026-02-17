package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

/**
 * DTO Request para Proveedor
 */
public class ProveedorRequest {

    private Long negocioId;
    private String codigo;
    private String razonSocial;
    private String nombreComercial;
    private String ruc;
    private String nombreContacto;
    private String telefonoContacto;
    private String emailContacto;
    private String direccion;
    private String ciudad;
    private String departamento;
    private String pais;
    private Integer plazoPagoDias;
    private BigDecimal limiteCredito;
    private String nombreBanco;
    private String numeroCuentaBancaria;
    private String cciBancario;
    private Integer calificacion;
    private String notas;
    private Boolean estaActivo;

    // ── Getters y Setters ──

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }

    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }

    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Integer getPlazoPagoDias() { return plazoPagoDias; }
    public void setPlazoPagoDias(Integer plazoPagoDias) { this.plazoPagoDias = plazoPagoDias; }

    public BigDecimal getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(BigDecimal limiteCredito) { this.limiteCredito = limiteCredito; }

    public String getNombreBanco() { return nombreBanco; }
    public void setNombreBanco(String nombreBanco) { this.nombreBanco = nombreBanco; }

    public String getNumeroCuentaBancaria() { return numeroCuentaBancaria; }
    public void setNumeroCuentaBancaria(String numeroCuentaBancaria) { this.numeroCuentaBancaria = numeroCuentaBancaria; }

    public String getCciBancario() { return cciBancario; }
    public void setCciBancario(String cciBancario) { this.cciBancario = cciBancario; }

    public Integer getCalificacion() { return calificacion; }
    public void setCalificacion(Integer calificacion) { this.calificacion = calificacion; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }
}
