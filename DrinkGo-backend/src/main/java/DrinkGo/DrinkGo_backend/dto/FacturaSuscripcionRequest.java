package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO Request para FacturaSuscripcion
 */
public class FacturaSuscripcionRequest {

    private Long suscripcionId;
    private Long negocioId;
    private String numeroFactura;
    private LocalDate inicioPeriodo;
    private LocalDate finPeriodo;
    private BigDecimal subtotal;
    private BigDecimal montoImpuesto;
    private BigDecimal montoDescuento;
    private BigDecimal total;
    private String moneda;
    private String estado; // borrador, pendiente, pagada, fallida, reembolsada, anulada
    private String metodoPago;
    private String referenciaPago;
    private String notas;
    private LocalDate fechaVencimiento;

    // ── Getters y Setters ──

    public Long getSuscripcionId() { return suscripcionId; }
    public void setSuscripcionId(Long suscripcionId) { this.suscripcionId = suscripcionId; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }

    public LocalDate getInicioPeriodo() { return inicioPeriodo; }
    public void setInicioPeriodo(LocalDate inicioPeriodo) { this.inicioPeriodo = inicioPeriodo; }

    public LocalDate getFinPeriodo() { return finPeriodo; }
    public void setFinPeriodo(LocalDate finPeriodo) { this.finPeriodo = finPeriodo; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getMontoImpuesto() { return montoImpuesto; }
    public void setMontoImpuesto(BigDecimal montoImpuesto) { this.montoImpuesto = montoImpuesto; }

    public BigDecimal getMontoDescuento() { return montoDescuento; }
    public void setMontoDescuento(BigDecimal montoDescuento) { this.montoDescuento = montoDescuento; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getReferenciaPago() { return referenciaPago; }
    public void setReferenciaPago(String referenciaPago) { this.referenciaPago = referenciaPago; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
