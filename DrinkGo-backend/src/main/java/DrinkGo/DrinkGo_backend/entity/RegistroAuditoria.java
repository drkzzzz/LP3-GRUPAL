package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entidad RegistroAuditoria - Registro de auditoría del sistema
 * RF-ADM-019
 */
@Entity
@Table(name = "registros_auditoria")
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id")
    private Long negocioId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "accion", nullable = false, length = 50)
    private String accion;

    @Column(name = "tipo_entidad", nullable = false, length = 100)
    private String tipoEntidad;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valores_anteriores", columnDefinition = "JSON")
    private Map<String, Object> valoresAnteriores;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valores_nuevos", columnDefinition = "JSON")
    private Map<String, Object> valoresNuevos;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "fecha_accion", nullable = false, updatable = false)
    private LocalDateTime fechaAccion;

    // ── Lifecycle Callbacks ──

    @PrePersist
    protected void onCreate() {
        this.fechaAccion = LocalDateTime.now();
    }

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getTipoEntidad() { return tipoEntidad; }
    public void setTipoEntidad(String tipoEntidad) { this.tipoEntidad = tipoEntidad; }

    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }

    public Map<String, Object> getValoresAnteriores() { return valoresAnteriores; }
    public void setValoresAnteriores(Map<String, Object> valoresAnteriores) { this.valoresAnteriores = valoresAnteriores; }

    public Map<String, Object> getValoresNuevos() { return valoresNuevos; }
    public void setValoresNuevos(Map<String, Object> valoresNuevos) { this.valoresNuevos = valoresNuevos; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public LocalDateTime getFechaAccion() { return fechaAccion; }
    public void setFechaAccion(LocalDateTime fechaAccion) { this.fechaAccion = fechaAccion; }
}
