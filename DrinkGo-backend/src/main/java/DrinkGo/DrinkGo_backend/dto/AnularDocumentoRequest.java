package DrinkGo.DrinkGo_backend.dto;

/**
 * Request para anular un documento de facturación.
 */
public class AnularDocumentoRequest {

    private Long anuladoPor;
    private String motivoAnulacion;

    // --- Getters y Setters ---

    public Long getAnuladoPor() { return anuladoPor; }
    public void setAnuladoPor(Long anuladoPor) { this.anuladoPor = anuladoPor; }

    public String getMotivoAnulacion() { return motivoAnulacion; }
    public void setMotivoAnulacion(String motivoAnulacion) { this.motivoAnulacion = motivoAnulacion; }
}
