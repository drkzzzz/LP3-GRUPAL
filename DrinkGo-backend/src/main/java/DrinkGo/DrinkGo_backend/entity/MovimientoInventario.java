package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Movimientos de inventario (RF-INV-004..006).
 * Mapeo exacto de la tabla movimientos_inventario de drinkgo_database.sql.
 * Registra cada entrada y salida con referencia al lote utilizado.
 */
@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    public enum TipoMovimiento {
        entrada_compra, salida_venta, entrada_devolucion, salida_devolucion,
        entrada_transferencia, salida_transferencia, ajuste_entrada, ajuste_salida,
        merma, rotura, vencimiento, stock_inicial, entrada_produccion, salida_produccion
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "producto_id", nullable = false, insertable = false, updatable = false)
    private Long productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "almacen_id", nullable = false, insertable = false, updatable = false)
    private Long almacenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @Column(name = "lote_id", insertable = false, updatable = false)
    private Long loteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private LoteInventario lote;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "costo_unitario", precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Column(name = "tipo_referencia", length = 50)
    private String tipoReferencia;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "motivo", length = 300)
    private String motivo;

    @Column(name = "realizado_por")
    private Long realizadoPor;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getProductoId() { return producto != null ? producto.getId() : productoId; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Long getAlmacenId() { return almacen != null ? almacen.getId() : almacenId; }

    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }

    public Long getLoteId() { return lote != null ? lote.getId() : loteId; }

    public LoteInventario getLote() { return lote; }
    public void setLote(LoteInventario lote) { this.lote = lote; }

    public TipoMovimiento getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }

    public String getTipoReferencia() { return tipoReferencia; }
    public void setTipoReferencia(String tipoReferencia) { this.tipoReferencia = tipoReferencia; }

    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Long getRealizadoPor() { return realizadoPor; }
    public void setRealizadoPor(Long realizadoPor) { this.realizadoPor = realizadoPor; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
