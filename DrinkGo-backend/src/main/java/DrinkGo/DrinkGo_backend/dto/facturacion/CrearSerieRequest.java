package DrinkGo.DrinkGo_backend.dto.facturacion;

/**
 * DTO para crear/actualizar una serie de facturación.
 */
public class CrearSerieRequest {

    private Long negocioId;
    private Long sedeId;
    private String tipoDocumento; // boleta | factura | nota_credito | nota_debito
    private String serie;         // B001, F001, etc.
    private Boolean esPredeterminada;

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }
    public Boolean getEsPredeterminada() { return esPredeterminada; }
    public void setEsPredeterminada(Boolean esPredeterminada) { this.esPredeterminada = esPredeterminada; }
}
