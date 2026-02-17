package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO Response para ProductoProveedor
 */
public class ProductoProveedorResponse {

    private Long id;
    private Long proveedorId;
    private Long productoId;
    private String skuProveedor;
    private BigDecimal precioProveedor;
    private Integer diasTiempoEntrega;
    private Integer cantidadMinimaPedido;
    private Boolean esPreferido;
    private LocalDate fechaUltimaCompra;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
