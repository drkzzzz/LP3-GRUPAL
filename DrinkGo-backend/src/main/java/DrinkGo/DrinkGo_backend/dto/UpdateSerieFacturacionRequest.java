package DrinkGo.DrinkGo_backend.dto;

/**
 * Request para actualizar una serie de facturación.
 * No permite cambiar negocioId, sedeId ni tipo_documento.
 */
public class UpdateSerieFacturacionRequest {

    private String prefijoSerie;
    private Boolean estaActivo;

    // --- Getters y Setters ---

    public String getPrefijoSerie() { return prefijoSerie; }
    public void setPrefijoSerie(String prefijoSerie) { this.prefijoSerie = prefijoSerie; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }
}
