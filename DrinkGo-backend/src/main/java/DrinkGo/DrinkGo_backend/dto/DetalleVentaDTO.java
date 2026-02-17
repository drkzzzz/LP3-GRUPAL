package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

/**
 * DTO para detalle de venta (item individual).
 * Bloque 8 - Ventas, POS y Cajas.
 */
public class DetalleVentaDTO {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoPorItem;
    private BigDecimal impuestoPorItem;
    private BigDecimal subtotalItem;
    private String observaciones;

    public DetalleVentaDTO() {
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getDescuentoPorItem() {
        return descuentoPorItem;
    }

    public void setDescuentoPorItem(BigDecimal descuentoPorItem) {
        this.descuentoPorItem = descuentoPorItem;
    }

    public BigDecimal getImpuestoPorItem() {
        return impuestoPorItem;
    }

    public void setImpuestoPorItem(BigDecimal impuestoPorItem) {
        this.impuestoPorItem = impuestoPorItem;
    }

    public BigDecimal getSubtotalItem() {
        return subtotalItem;
    }

    public void setSubtotalItem(BigDecimal subtotalItem) {
        this.subtotalItem = subtotalItem;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
