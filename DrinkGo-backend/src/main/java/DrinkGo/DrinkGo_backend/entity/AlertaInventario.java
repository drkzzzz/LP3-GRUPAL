package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Alertas de inventario automáticas (RF-INV-004..005).
 * NOTA: tabla alertas_inventario NO tiene columna eliminado_en.
 */
@Entity
@Table(name = "alertas_inventario")
public class AlertaInventario {

    public enum TipoAlerta {
        stock_bajo, sobrestock, proximo_vencer, vencido, punto_reorden
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

    @Column(name = "almacen_id")
    private Long almacenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", insertable = false, updatable = false)
    private Almacen almacen;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alerta", nullable = false)
    private TipoAlerta tipoAlerta;

    @Column(name = "mensaje", nullable = false, length = 500)
    private String mensaje;

    @Column(name = "valor_umbral")
    private Integer valorUmbral;

    @Column(name = "valor_actual")
    private Integer valorActual;

    @Column(name = "esta_resuelta", nullable = false)
    private Boolean estaResuelta = false;

    @Column(name = "resuelta_en")
    private LocalDateTime resueltaEn;

    @Column(name = "resuelta_por")
    private Long resueltaPor;

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

    public TipoAlerta getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(TipoAlerta tipoAlerta) { this.tipoAlerta = tipoAlerta; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Integer getValorUmbral() { return valorUmbral; }
    public void setValorUmbral(Integer valorUmbral) { this.valorUmbral = valorUmbral; }

    public Integer getValorActual() { return valorActual; }
    public void setValorActual(Integer valorActual) { this.valorActual = valorActual; }

    public Boolean getEstaResuelta() { return estaResuelta; }
    public void setEstaResuelta(Boolean estaResuelta) { this.estaResuelta = estaResuelta; }

    public LocalDateTime getResueltaEn() { return resueltaEn; }
    public void setResueltaEn(LocalDateTime resueltaEn) { this.resueltaEn = resueltaEn; }

    public Long getResueltaPor() { return resueltaPor; }
    public void setResueltaPor(Long resueltaPor) { this.resueltaPor = resueltaPor; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}