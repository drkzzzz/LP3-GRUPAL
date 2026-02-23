package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FacturasSuscripcionDTO {

    private Long id;
    private Long suscripcionId;
    private String numeroFactura;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private String estadoPago;
    private LocalDateTime pagadoEn;
    private String metodoPago;
    private String transaccionId;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public FacturasSuscripcionDTO() {
    }

    public FacturasSuscripcionDTO(Long id, Long suscripcionId, String numeroFactura, LocalDate fechaEmision,
            LocalDate fechaVencimiento, BigDecimal subtotal, BigDecimal impuestos, BigDecimal total,
            String estadoPago, LocalDateTime pagadoEn, String metodoPago, String transaccionId,
            LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.suscripcionId = suscripcionId;
        this.numeroFactura = numeroFactura;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.subtotal = subtotal;
        this.impuestos = impuestos;
        this.total = total;
        this.estadoPago = estadoPago;
        this.pagadoEn = pagadoEn;
        this.metodoPago = metodoPago;
        this.transaccionId = transaccionId;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSuscripcionId() {
        return suscripcionId;
    }

    public void setSuscripcionId(Long suscripcionId) {
        this.suscripcionId = suscripcionId;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public LocalDateTime getPagadoEn() {
        return pagadoEn;
    }

    public void setPagadoEn(LocalDateTime pagadoEn) {
        this.pagadoEn = pagadoEn;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getTransaccionId() {
        return transaccionId;
    }

    public void setTransaccionId(String transaccionId) {
        this.transaccionId = transaccionId;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
