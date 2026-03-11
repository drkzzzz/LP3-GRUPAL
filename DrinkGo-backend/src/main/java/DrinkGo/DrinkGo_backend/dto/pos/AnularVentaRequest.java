package DrinkGo.DrinkGo_backend.dto.pos;


/**
 * DTO para anular una venta.
 */
public class AnularVentaRequest {

    private Long ventaId;
    private Long usuarioId;
    private String razonCancelacion;
    private boolean skipAnularComprobantes;

    public Long getVentaId() { return ventaId; }
    public void setVentaId(Long ventaId) { this.ventaId = ventaId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getRazonCancelacion() { return razonCancelacion; }
    public void setRazonCancelacion(String razonCancelacion) { this.razonCancelacion = razonCancelacion; }
    public boolean isSkipAnularComprobantes() { return skipAnularComprobantes; }
    public void setSkipAnularComprobantes(boolean skipAnularComprobantes) { this.skipAnularComprobantes = skipAnularComprobantes; }
}
