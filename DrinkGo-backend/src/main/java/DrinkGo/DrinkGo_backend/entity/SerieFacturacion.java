package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity para series de facturación por sede.
 * Tabla: series_facturacion
 * RF-FACT-001: Series de facturación electrónica
 */
@Entity
@Table(name = "series_facturacion")
public class SerieFacturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Column(name = "tipo_documento", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;

    @Column(name = "prefijo_serie", nullable = false, length = 10)
    private String prefijoSerie;

    @Column(name = "numero_actual", nullable = false)
    private Integer numeroActual = 0;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    // --- ENUMS ---

    public enum TipoDocumento {
        boleta,
        factura,
        nota_credito,
        nota_debito,
        guia_remision
    }

    // --- Constructores ---

    public SerieFacturacion() {
    }

    // --- PrePersist & PreUpdate ---

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

    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getPrefijoSerie() { return prefijoSerie; }
    public void setPrefijoSerie(String prefijoSerie) { this.prefijoSerie = prefijoSerie; }

    public Integer getNumeroActual() { return numeroActual; }
    public void setNumeroActual(Integer numeroActual) { this.numeroActual = numeroActual; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
