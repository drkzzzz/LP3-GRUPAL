package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_devoluciones")
@JsonPropertyOrder({ "id", "devolucionId", "productoId", "detalleVentaId", "detallePedidoId", "cantidad",
        "precioUnitario", "total", "estadoCondicion", "devolverStock", "almacenId", "notas" })
public class DetalleDevoluciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucion_id", nullable = false)
    private Devoluciones devolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Productos producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_venta_id")
    private DetalleVentas detalleVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_pedido_id")
    private DetallePedidos detallePedido;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_condicion", nullable = false)
    private EstadoCondicion estadoCondicion = EstadoCondicion.bueno;

    @Column(name = "devolver_stock", nullable = false)
    private Boolean devolverStock = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id")
    private Almacenes almacen;

    @Column(length = 300)
    private String notas;

    public enum EstadoCondicion {
        bueno, danado, vencido, abierto
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Devoluciones getDevolucion() {
        return devolucion;
    }

    public void setDevolucion(Devoluciones devolucion) {
        this.devolucion = devolucion;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }

    public DetalleVentas getDetalleVenta() {
        return detalleVenta;
    }

    public void setDetalleVenta(DetalleVentas detalleVenta) {
        this.detalleVenta = detalleVenta;
    }

    public DetallePedidos getDetallePedido() {
        return detallePedido;
    }

    public void setDetallePedido(DetallePedidos detallePedido) {
        this.detallePedido = detallePedido;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public EstadoCondicion getEstadoCondicion() {
        return estadoCondicion;
    }

    public void setEstadoCondicion(EstadoCondicion estadoCondicion) {
        this.estadoCondicion = estadoCondicion;
    }

    public Boolean getDevolverStock() {
        return devolverStock;
    }

    public void setDevolverStock(Boolean devolverStock) {
        this.devolverStock = devolverStock;
    }

    public Almacenes getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacenes almacen) {
        this.almacen = almacen;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    @Override
    public String toString() {
        return "DetalleDevoluciones [id=" + id + ", cantidad=" + cantidad + ", precioUnitario=" + precioUnitario
                + ", total=" + total + ", estadoCondicion=" + estadoCondicion + "]";
    }
}
