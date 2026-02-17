package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO Response para FacturaSuscripcion
 */
public class FacturaSuscripcionResponse {

    private Long id;
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
    private String estado;
    private String metodoPago;
    private String referenciaPago;
    private LocalDateTime pagadoEn;
    private Integer intentosReintento;
    private LocalDateTime proximoReintentoEn;
    private String notas;
    private LocalDateTime emitidoEn;
    private LocalDate fechaVencimiento;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getPagadoEn() { return pagadoEn; }
    public void setPagadoEn(LocalDateTime pagadoEn) { this.pagadoEn = pagadoEn; }

    public Integer getIntentosReintento() { return intentosReintento; }
    public void setIntentosReintento(Integer intentosReintento) { this.intentosReintento = intentosReintento; }

    public LocalDateTime getProximoReintentoEn() { return proximoReintentoEn; }
    public void setProximoReintentoEn(LocalDateTime proximoReintentoEn) { this.proximoReintentoEn = proximoReintentoEn; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public LocalDateTime getEmitidoEn() { return emitidoEn; }
    public void setEmitidoEn(LocalDateTime emitidoEn) { this.emitidoEn = emitidoEn; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
