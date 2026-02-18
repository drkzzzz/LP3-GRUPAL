package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad Negocio - Representa las licorerías/tenants en el sistema multi-tenant.
 * RF-PLT-001, RF-ADM-001, RF-ADM-002
 */
@Entity
@Table(name = "negocios")
@SQLDelete(sql = "UPDATE negocios SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class Negocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, length = 36)
    private String uuid;

    @Column(name = "razon_social", nullable = false, length = 200)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial;

    @Column(name = "ruc", length = 20)
    private String ruc;

    @Column(name = "tipo_documento_fiscal", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private TipoDocumentoFiscal tipoDocumentoFiscal = TipoDocumentoFiscal.RUC;

    @Column(name = "representante_legal", length = 200)
    private String representanteLegal;

    @Column(name = "documento_representante", length = 20)
    private String documentoRepresentante;

    @Column(name = "tipo_negocio", length = 100)
    private String tipoNegocio;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "direccion", length = 300)
    private String direccion;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "departamento", length = 100)
    private String departamento;

    @Column(name = "pais", nullable = false, length = 3)
    private String pais = "PE";

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Column(name = "url_logo", length = 500)
    private String urlLogo;

    @Column(name = "estado", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoNegocio estado = EstadoNegocio.pendiente;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    // ── Enums ──

    public enum TipoDocumentoFiscal {
        RUC, DNI, CE, OTRO
    }

    public enum EstadoNegocio {
        activo, suspendido, cancelado, pendiente
    }

    // ── Lifecycle Callbacks ──

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public TipoDocumentoFiscal getTipoDocumentoFiscal() { return tipoDocumentoFiscal; }
    public void setTipoDocumentoFiscal(TipoDocumentoFiscal tipoDocumentoFiscal) { this.tipoDocumentoFiscal = tipoDocumentoFiscal; }

    public String getRepresentanteLegal() { return representanteLegal; }
    public void setRepresentanteLegal(String representanteLegal) { this.representanteLegal = representanteLegal; }

    public String getDocumentoRepresentante() { return documentoRepresentante; }
    public void setDocumentoRepresentante(String documentoRepresentante) { this.documentoRepresentante = documentoRepresentante; }

    public String getTipoNegocio() { return tipoNegocio; }
    public void setTipoNegocio(String tipoNegocio) { this.tipoNegocio = tipoNegocio; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getUrlLogo() { return urlLogo; }
    public void setUrlLogo(String urlLogo) { this.urlLogo = urlLogo; }

    public EstadoNegocio getEstado() { return estado; }
    public void setEstado(EstadoNegocio estado) { this.estado = estado; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }

    public LocalDateTime getEliminadoEn() { return eliminadoEn; }
    public void setEliminadoEn(LocalDateTime eliminadoEn) { this.eliminadoEn = eliminadoEn; }
}
