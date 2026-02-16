package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

/**
 * Alertas de inventario automáticas (RF-INV-004..005).
 * Genera alertas de stock bajo, próximo a vencer, vencido, etc.
 */
@Entity
@Table(name = "alertas_inventario")
@SQLDelete(sql = "UPDATE alertas_inventario SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class AlertaInventario {

    public enum TipoAlerta {
        stock_bajo, sobrestock, proximo_vencer, vencido, punto_reorden
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id")
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

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }

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

    public LocalDateTime getEliminadoEn() { return eliminadoEn; }
    public void setEliminadoEn(LocalDateTime eliminadoEn) { this.eliminadoEn = eliminadoEn; }
}