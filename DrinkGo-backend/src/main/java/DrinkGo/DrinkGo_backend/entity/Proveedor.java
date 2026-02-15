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
 * Entidad Proveedor - Mapea la tabla 'proveedores' de drinkgo_database.sql.
 * Bloque 6: Proveedores y Compras (RF-COM-001..003).
 * Borrado lógico: campo esta_activo = 0 (soft delete).
 */
@Entity
@Table(name = "proveedores")
@SQLDelete(sql = "UPDATE proveedores SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;

    @Column(name = "razon_social", nullable = false, length = 200)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial;

    @Column(name = "ruc", length = 20)
    private String ruc;

    @Column(name = "nombre_contacto", length = 150)
    private String nombreContacto;

    @Column(name = "telefono_contacto", length = 30)
    private String telefonoContacto;

    @Column(name = "email_contacto", length = 150)
    private String emailContacto;

    @Column(name = "direccion", length = 300)
    private String direccion;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "departamento", length = 100)
    private String departamento;

    @Column(name = "pais", nullable = false, length = 3)
    private String pais = "PE";

    @Column(name = "plazo_pago_dias", nullable = false)
    private Integer plazoPagoDias = 30;

    @Column(name = "limite_credito", precision = 12, scale = 2)
    private BigDecimal limiteCredito;

    @Column(name = "nombre_banco", length = 100)
    private String nombreBanco;

    @Column(name = "numero_cuenta_bancaria", length = 50)
    private String numeroCuentaBancaria;

    @Column(name = "cci_bancario", length = 50)
    private String cciBancario;

    @Column(name = "calificacion")
    private Integer calificacion;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}