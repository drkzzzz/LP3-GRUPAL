package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad ProductoProveedor - Relación entre productos y proveedores 
 * Tabla: productos_proveedor
 */
@Entity
@Table(name = "productos_proveedor")
public class ProductoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proveedor_id", nullable = false)
    private Long proveedorId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "sku_proveedor", length = 50)
    private String skuProveedor;

    @Column(name = "precio_proveedor", precision = 10, scale = 2)
    private BigDecimal precioProveedor;

    @Column(name = "dias_tiempo_entrega")
    private Integer diasTiempoEntrega;

    @Column(name = "cantidad_minima_pedido")
    private Integer cantidadMinimaPedido;

    @Column(name = "es_preferido")
    private Boolean esPreferido = false;

    @Column(name = "fecha_ultima_compra")
    private LocalDate fechaUltimaCompra;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

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
