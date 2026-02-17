package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stock actual por producto y almacén (RF-INV-001).
 * La columna cantidad_disponible es calculada por la base de datos (mano - reservada).
 * NOTA: la tabla stock_inventario NO tiene columna eliminado_en.
 */
@Entity
@Table(name = "stock_inventario")
public class StockInventario {

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

    @Column(name = "cantidad_en_mano", nullable = false)
    private Integer cantidadEnMano = 0;

    @Column(name = "cantidad_reservada", nullable = false)
    private Integer cantidadReservada = 0;

    /** Columna generada por MySQL: cantidad_en_mano - cantidad_reservada */
    @Column(name = "cantidad_disponible", insertable = false, updatable = false)
    private Integer cantidadDisponible;

    @Column(name = "ultimo_conteo_en")
    private LocalDateTime ultimoConteoEn;

    @Column(name = "ultimo_movimiento_en")
    private LocalDateTime ultimoMovimientoEn;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
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

    public Integer getCantidadEnMano() { return cantidadEnMano; }
    public void setCantidadEnMano(Integer cantidadEnMano) { this.cantidadEnMano = cantidadEnMano; }

    public Integer getCantidadReservada() { return cantidadReservada; }
    public void setCantidadReservada(Integer cantidadReservada) { this.cantidadReservada = cantidadReservada; }

    public Integer getCantidadDisponible() { return cantidadDisponible; }

    public LocalDateTime getUltimoConteoEn() { return ultimoConteoEn; }
    public void setUltimoConteoEn(LocalDateTime ultimoConteoEn) { this.ultimoConteoEn = ultimoConteoEn; }

    public LocalDateTime getUltimoMovimientoEn() { return ultimoMovimientoEn; }
    public void setUltimoMovimientoEn(LocalDateTime ultimoMovimientoEn) { this.ultimoMovimientoEn = ultimoMovimientoEn; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}