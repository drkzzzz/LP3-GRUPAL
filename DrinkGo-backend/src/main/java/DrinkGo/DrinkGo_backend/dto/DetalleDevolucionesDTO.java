package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DetalleDevolucionesDTO {

    private Long id;
    private Long devolucionId;
    private Long productoId;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String motivoDetalle;
    private LocalDateTime creadoEn;

    // Constructores
    public DetalleDevolucionesDTO() {
    }

    public DetalleDevolucionesDTO(Long id, Long devolucionId, Long productoId, BigDecimal cantidad,
            BigDecimal precioUnitario, BigDecimal subtotal, String motivoDetalle, LocalDateTime creadoEn) {
        this.id = id;
        this.devolucionId = devolucionId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.motivoDetalle = motivoDetalle;
        this.creadoEn = creadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDevolucionId() {
        return devolucionId;
    }

    public void setDevolucionId(Long devolucionId) {
        this.devolucionId = devolucionId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getMotivoDetalle() {
        return motivoDetalle;
    }

    public void setMotivoDetalle(String motivoDetalle) {
        this.motivoDetalle = motivoDetalle;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
