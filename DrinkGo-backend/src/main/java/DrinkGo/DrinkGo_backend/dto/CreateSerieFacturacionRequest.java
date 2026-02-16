package DrinkGo.DrinkGo_backend.dto;

/**
 * Request para crear una nueva serie de facturación.
 */
public class CreateSerieFacturacionRequest {

    private Long negocioId;
    private Long sedeId;
    private String tipoDocumento; // boleta, factura, nota_credito, nota_debito, guia_remision
    private String prefijoSerie;  // Ej: B001, F001, BC01, FC01

    // --- Getters y Setters ---

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getPrefijoSerie() { return prefijoSerie; }
    public void setPrefijoSerie(String prefijoSerie) { this.prefijoSerie = prefijoSerie; }
}
