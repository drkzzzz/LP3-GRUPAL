package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO Request para HistorialConfiguracionPlataforma
 */
public class HistorialConfiguracionPlataformaRequest {

    private Long configuracionId;
    private String valorAnterior;
    private String valorNuevo;
    private Long cambiadoPor;
    private String razonCambio;

    // ── Getters y Setters ──

    public Long getConfiguracionId() { return configuracionId; }
    public void setConfiguracionId(Long configuracionId) { this.configuracionId = configuracionId; }

    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }

    public String getValorNuevo() { return valorNuevo; }
    public void setValorNuevo(String valorNuevo) { this.valorNuevo = valorNuevo; }

    public Long getCambiadoPor() { return cambiadoPor; }
    public void setCambiadoPor(Long cambiadoPor) { this.cambiadoPor = cambiadoPor; }

    public String getRazonCambio() { return razonCambio; }
    public void setRazonCambio(String razonCambio) { this.razonCambio = razonCambio; }
}
