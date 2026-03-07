package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@SQLDelete(sql = "UPDATE clientes SET esta_activo = 0, eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "tipoDocumento", "numeroDocumento", "nombres", "apellidos", "razonSocial",
        "nombreComercial", "email", "telefono", "fechaNacimiento", "direccion",
        "totalCompras", "ultimaCompraEn", "estaActivo", "creadoEn", "actualizadoEn",
        "eliminadoEn" })
public class Clientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @Column(name = "negocio_id", insertable = false, updatable = false)
    private Long negocioId;

    @Column(name = "uuid", unique = true, nullable = false, length = 36)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento")
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    private String nombres;

    private String apellidos;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "nombre_comercial")
    private String nombreComercial;

    private String email;

    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String direccion;

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "total_compras", precision = 10, scale = 2)
    private BigDecimal totalCompras = BigDecimal.ZERO;

    @Column(name = "ultima_compra_en")
    private LocalDateTime ultimaCompraEn;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    public enum TipoDocumento {
        DNI, RUC, CE, PASAPORTE, OTRO
    }

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = java.util.UUID.randomUUID().toString();
        }
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
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

    @JsonIgnore
    public Negocios getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocios negocio) {
        this.negocio = negocio;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public BigDecimal getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(BigDecimal totalCompras) {
        this.totalCompras = totalCompras;
    }

    public LocalDateTime getUltimaCompraEn() {
        return ultimaCompraEn;
    }

    public void setUltimaCompraEn(LocalDateTime ultimaCompraEn) {
        this.ultimaCompraEn = ultimaCompraEn;
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
        return "Clientes [id=" + id + ", numeroDocumento=" + numeroDocumento + ", nombres=" + nombres + ", apellidos="
                + apellidos + ", estaActivo=" + estaActivo + "]";
    }
}
