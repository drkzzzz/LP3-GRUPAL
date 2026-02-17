package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad VersionPlanSuscripcion - Versionado de planes de suscripción
 * RF-PLT-003
 */
@Entity
@Table(name = "versiones_planes_suscripcion")
public class VersionPlanSuscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "snapshot_json", nullable = false, columnDefinition = "JSON")
    private String snapshotJson;

    @Column(name = "cambiado_por")
    private Long cambiadoPor;

    @Column(name = "razon_cambio", length = 500)
    private String razonCambio;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    // ── Lifecycle Callbacks ──

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public String getSnapshotJson() { return snapshotJson; }
    public void setSnapshotJson(String snapshotJson) { this.snapshotJson = snapshotJson; }

    public Long getCambiadoPor() { return cambiadoPor; }
    public void setCambiadoPor(Long cambiadoPor) { this.cambiadoPor = cambiadoPor; }

    public String getRazonCambio() { return razonCambio; }
    public void setRazonCambio(String razonCambio) { this.razonCambio = razonCambio; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
