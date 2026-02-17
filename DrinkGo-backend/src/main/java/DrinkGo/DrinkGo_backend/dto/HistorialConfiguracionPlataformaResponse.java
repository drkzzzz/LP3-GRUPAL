package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

/**
 * DTO Response para HistorialConfiguracionPlataforma
 */
public class HistorialConfiguracionPlataformaResponse {

    private Long id;
    private Long configuracionId;
    private String valorAnterior;
    private String valorNuevo;
    private Long cambiadoPor;
    private String razonCambio;
    private LocalDateTime creadoEn;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
