package DrinkGo.DrinkGo_backend.dto.pos;

import java.math.BigDecimal;

/**
 * Representa un item (producto) dentro de una venta POS.
 */
public class ItemVentaPosDTO {

    private Long productoId;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private BigDecimal cantidad;
    private BigDecimal descuento;

    public ItemVentaPosDTO() {}

    public ItemVentaPosDTO(Long productoId, String nombreProducto, BigDecimal precioUnitario,
                           BigDecimal cantidad, BigDecimal descuento) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.descuento = descuento;
    }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
}
