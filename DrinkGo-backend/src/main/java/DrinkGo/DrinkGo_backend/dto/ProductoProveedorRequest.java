package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO Request para ProductoProveedor
 */
public class ProductoProveedorRequest {

    private Long proveedorId;
    private Long productoId;
    private String skuProveedor;
    private BigDecimal precioProveedor;
    private Integer diasTiempoEntrega;
    private Integer cantidadMinimaPedido;
    private Boolean esPreferido;
    private LocalDate fechaUltimaCompra;

    // ── Getters y Setters ──

    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getSkuProveedor() { return skuProveedor; }
    public void setSkuProveedor(String skuProveedor) { this.skuProveedor = skuProveedor; }

    public BigDecimal getPrecioProveedor() { return precioProveedor; }
    public void setPrecioProveedor(BigDecimal precioProveedor) { this.precioProveedor = precioProveedor; }

    public Integer getDiasTiempoEntrega() { return diasTiempoEntrega; }
    public void setDiasTiempoEntrega(Integer diasTiempoEntrega) { this.diasTiempoEntrega = diasTiempoEntrega; }

    public Integer getCantidadMinimaPedido() { return cantidadMinimaPedido; }
    public void setCantidadMinimaPedido(Integer cantidadMinimaPedido) { this.cantidadMinimaPedido = cantidadMinimaPedido; }

    public Boolean getEsPreferido() { return esPreferido; }
    public void setEsPreferido(Boolean esPreferido) { this.esPreferido = esPreferido; }

    public LocalDate getFechaUltimaCompra() { return fechaUltimaCompra; }
    public void setFechaUltimaCompra(LocalDate fechaUltimaCompra) { this.fechaUltimaCompra = fechaUltimaCompra; }
}
