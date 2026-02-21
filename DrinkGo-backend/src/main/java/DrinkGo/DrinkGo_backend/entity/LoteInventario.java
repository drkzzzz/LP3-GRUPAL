package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Gestión de lotes y fechas de expiración (Bloque 5.2).
 * FK compuesta (producto_id, almacen_id) referencia a stock_inventario.
 * Tabla: lotes_inventario
 */
@Entity
@Table(name = "lotes_inventario")
public class LoteInventario {

    public enum LoteEstado {
        disponible, agotado, vencido
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", insertable = false, updatable = false)
    private Producto producto;

    @Column(name = "almacen_id", nullable = false)
    private Long almacenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", insertable = false, updatable = false)
    private Almacen almacen;

    @Column(name = "numero_lote", nullable = false, length = 50)
    private String numeroLote;

    @Column(name = "cantidad_restante", nullable = false)
    private Integer cantidadRestante;

    @Column(name = "costo_unitario_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitarioCompra;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private LoteEstado estado = LoteEstado.disponible;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) {
        this.producto = producto;
        if (producto != null) this.productoId = producto.getId();
    }

    public Long getAlmacenId() { return almacenId; }
    public void setAlmacenId(Long almacenId) { this.almacenId = almacenId; }

    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
        if (almacen != null) this.almacenId = almacen.getId();
    }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public Integer getCantidadRestante() { return cantidadRestante; }
    public void setCantidadRestante(Integer cantidadRestante) { this.cantidadRestante = cantidadRestante; }

    public BigDecimal getCostoUnitarioCompra() { return costoUnitarioCompra; }
    public void setCostoUnitarioCompra(BigDecimal costoUnitarioCompra) { this.costoUnitarioCompra = costoUnitarioCompra; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public LoteEstado getEstado() { return estado; }
    public void setEstado(LoteEstado estado) { this.estado = estado; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
