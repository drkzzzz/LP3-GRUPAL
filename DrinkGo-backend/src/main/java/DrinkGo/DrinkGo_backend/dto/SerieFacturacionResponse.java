package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

/**
 * Response para series de facturación.
 */
public class SerieFacturacionResponse {

    private Long id;
    private Long negocioId;
    private Long sedeId;
    private String tipoDocumento;
    private String prefijoSerie;
    private Integer numeroActual;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getPrefijoSerie() { return prefijoSerie; }
    public void setPrefijoSerie(String prefijoSerie) { this.prefijoSerie = prefijoSerie; }

    public Integer getNumeroActual() { return numeroActual; }
    public void setNumeroActual(Integer numeroActual) { this.numeroActual = numeroActual; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
