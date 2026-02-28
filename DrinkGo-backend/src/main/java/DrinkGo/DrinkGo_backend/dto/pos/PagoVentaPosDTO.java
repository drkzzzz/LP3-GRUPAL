package DrinkGo.DrinkGo_backend.dto.pos;

import java.math.BigDecimal;

/**
 * Representa un pago dentro de una venta POS (soporta pagos mixtos).
 */
public class PagoVentaPosDTO {

    private Long metodoPagoId;
    private String tipoReferencia; // efectivo, yape, plin, tarjeta, transferencia
    private BigDecimal monto;
    private String numeroReferencia;

    public PagoVentaPosDTO() {}

    public PagoVentaPosDTO(Long metodoPagoId, String tipoReferencia, BigDecimal monto, String numeroReferencia) {
        this.metodoPagoId = metodoPagoId;
        this.tipoReferencia = tipoReferencia;
        this.monto = monto;
        this.numeroReferencia = numeroReferencia;
    }

    public Long getMetodoPagoId() { return metodoPagoId; }
    public void setMetodoPagoId(Long metodoPagoId) { this.metodoPagoId = metodoPagoId; }
    public String getTipoReferencia() { return tipoReferencia; }
    public void setTipoReferencia(String tipoReferencia) { this.tipoReferencia = tipoReferencia; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getNumeroReferencia() { return numeroReferencia; }
    public void setNumeroReferencia(String numeroReferencia) { this.numeroReferencia = numeroReferencia; }
}
