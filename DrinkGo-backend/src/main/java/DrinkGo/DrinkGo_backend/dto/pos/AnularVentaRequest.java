package DrinkGo.DrinkGo_backend.dto.pos;

import java.math.BigDecimal;

/**
 * Request para anular una venta POS.
 */
public class AnularVentaRequest {

    private Long ventaId;
    private Long usuarioId;
    private String razonCancelacion;

    public AnularVentaRequest() {}

    public Long getVentaId() { return ventaId; }
    public void setVentaId(Long ventaId) { this.ventaId = ventaId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getRazonCancelacion() { return razonCancelacion; }
    public void setRazonCancelacion(String razonCancelacion) { this.razonCancelacion = razonCancelacion; }
}
