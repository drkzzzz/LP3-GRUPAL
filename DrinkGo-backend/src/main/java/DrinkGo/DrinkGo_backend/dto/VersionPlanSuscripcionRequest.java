package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO Request para VersionPlanSuscripcion
 */
public class VersionPlanSuscripcionRequest {

    private Long planId;
    private Integer version;
    private String snapshotJson;
    private Long cambiadoPor;
    private String razonCambio;

    // ── Getters y Setters ──

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
}
