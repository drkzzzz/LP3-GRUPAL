package DrinkGo.DrinkGo_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad DireccionCliente - Mapea la tabla 'direcciones_cliente'.
 * Bloque 7: Direcciones de envío/contacto de clientes (RF-CLI-001).
 * Borrado lógico: esta_activo = 0 vía @SQLDelete.
 */
@Entity
@Table(name = "direcciones_cliente")
@SQLDelete(sql = "UPDATE direcciones_cliente SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
public class DireccionCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "etiqueta", length = 50)
    private String etiqueta;

    @Column(name = "direccion", nullable = false, length = 300)
    private String direccion;

    @Column(name = "direccion_2", length = 300)
    private String direccion2;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "departamento", length = 100)
    private String departamento;

    @Column(name = "pais", nullable = false, length = 3)
    private String pais = "PE";

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Column(name = "latitud", precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 11, scale = 8)
    private BigDecimal longitud;

    @Column(name = "referencia", length = 300)
    private String referencia;

    @Column(name = "telefono_contacto", length = 30)
    private String telefonoContacto;

    @Column(name = "es_predeterminado", nullable = false)
    private Boolean esPredeterminado = false;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

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

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}