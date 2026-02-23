package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_auditoria")
@JsonPropertyOrder({ "id", "negocioId", "usuarioId", "usuarioPlataformaId", "accion", "tipoEntidad", "entidadId",
        "valoresAnteriores", "valoresNuevos", "direccionIp", "agenteUsuario", "modulo", "descripcion", "severidad",
        "creadoEn" })
public class RegistrosAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id")
    private Long negocioId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_plataforma_id")
    private Long usuarioPlataformaId;

    @Column(nullable = false)
    private String accion;

    @Column(name = "tipo_entidad", nullable = false)
    private String tipoEntidad;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(name = "valores_anteriores", columnDefinition = "JSON")
    private String valoresAnteriores;

    @Column(name = "valores_nuevos", columnDefinition = "JSON")
    private String valoresNuevos;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @Column(name = "agente_usuario", length = 500)
    private String agenteUsuario;

    private String modulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Severidad severidad = Severidad.info;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    public enum Severidad {
        info, advertencia, critico
    }

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getUsuarioPlataformaId() {
        return usuarioPlataformaId;
    }

    public void setUsuarioPlataformaId(Long usuarioPlataformaId) {
        this.usuarioPlataformaId = usuarioPlataformaId;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(String tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
    }

    public String getValoresAnteriores() {
        return valoresAnteriores;
    }

    public void setValoresAnteriores(String valoresAnteriores) {
        this.valoresAnteriores = valoresAnteriores;
    }

    public String getValoresNuevos() {
        return valoresNuevos;
    }

    public void setValoresNuevos(String valoresNuevos) {
        this.valoresNuevos = valoresNuevos;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getAgenteUsuario() {
        return agenteUsuario;
    }

    public void setAgenteUsuario(String agenteUsuario) {
        this.agenteUsuario = agenteUsuario;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Severidad getSeveridad() {
        return severidad;
    }

    public void setSeveridad(Severidad severidad) {
        this.severidad = severidad;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    @Override
    public String toString() {
        return "RegistrosAuditoria [id=" + id + ", negocioId=" + negocioId + ", usuarioId=" + usuarioId
                + ", usuarioPlataformaId=" + usuarioPlataformaId + ", accion=" + accion + ", tipoEntidad=" + tipoEntidad
                + ", entidadId=" + entidadId + ", direccionIp=" + direccionIp + ", modulo=" + modulo + ", descripcion="
                + descripcion + ", severidad=" + severidad + ", creadoEn=" + creadoEn + "]";
    }
}
