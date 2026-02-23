package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "negocios")
@SQLDelete(sql = "UPDATE negocios SET esta_activo = 0, eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "uuid", "razonSocial", "nombreComercial", "ruc", "tipoDocumentoFiscal",
        "representanteLegal", "documentoRepresentante", "tipoNegocio", "email", "telefono", "direccion",
        "ciudad", "departamento", "pais", "codigoPostal", "urlLogo", "estado", "estaActivo", "creadoEn",
        "actualizadoEn", "eliminadoEn" })
public class Negocios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 36)
    private String uuid;

    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @Column(name = "nombre_comercial")
    private String nombreComercial;

    private String ruc;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento_fiscal")
    private TipoDocumentoFiscal tipoDocumentoFiscal;

    @Column(name = "representante_legal")
    private String representanteLegal;

    @Column(name = "documento_representante")
    private String documentoRepresentante;

    @Column(name = "tipo_negocio")
    private String tipoNegocio;

    @Column(nullable = false)
    private String email;

    private String telefono;

    private String direccion;

    private String ciudad;

    private String departamento;

    private String pais = "PE";

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "url_logo")
    private String urlLogo;

    @Enumerated(EnumType.STRING)
    private EstadoNegocio estado = EstadoNegocio.pendiente;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    public enum TipoDocumentoFiscal {
        RUC, DNI, CE, OTRO
    }

    public enum EstadoNegocio {
        activo, suspendido, cancelado, pendiente
    }

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
        if (uuid == null) {
            uuid = java.util.UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public TipoDocumentoFiscal getTipoDocumentoFiscal() {
        return tipoDocumentoFiscal;
    }

    public void setTipoDocumentoFiscal(TipoDocumentoFiscal tipoDocumentoFiscal) {
        this.tipoDocumentoFiscal = tipoDocumentoFiscal;
    }

    public String getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(String representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public String getDocumentoRepresentante() {
        return documentoRepresentante;
    }

    public void setDocumentoRepresentante(String documentoRepresentante) {
        this.documentoRepresentante = documentoRepresentante;
    }

    public String getTipoNegocio() {
        return tipoNegocio;
    }

    public void setTipoNegocio(String tipoNegocio) {
        this.tipoNegocio = tipoNegocio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
    }

    public EstadoNegocio getEstado() {
        return estado;
    }

    public void setEstado(EstadoNegocio estado) {
        this.estado = estado;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public LocalDateTime getEliminadoEn() {
        return eliminadoEn;
    }

    public void setEliminadoEn(LocalDateTime eliminadoEn) {
        this.eliminadoEn = eliminadoEn;
    }

    @Override
    public String toString() {
        return "Negocios [id=" + id + ", uuid=" + uuid + ", razonSocial=" + razonSocial + ", nombreComercial="
                + nombreComercial + ", ruc=" + ruc + ", tipoDocumentoFiscal=" + tipoDocumentoFiscal
                + ", representanteLegal=" + representanteLegal + ", documentoRepresentante=" + documentoRepresentante
                + ", tipoNegocio=" + tipoNegocio + ", email=" + email + ", telefono=" + telefono + ", direccion="
                + direccion + ", ciudad=" + ciudad + ", departamento=" + departamento + ", pais=" + pais
                + ", codigoPostal=" + codigoPostal + ", urlLogo=" + urlLogo + ", estado=" + estado + ", estaActivo="
                + estaActivo + ", creadoEn=" + creadoEn + ", actualizadoEn=" + actualizadoEn + ", eliminadoEn="
                + eliminadoEn + "]";
    }
}
