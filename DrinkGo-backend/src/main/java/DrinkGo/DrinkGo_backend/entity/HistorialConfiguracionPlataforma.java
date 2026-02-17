package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad HistorialConfiguracionPlataforma - Historial de cambios de configuración global
 * RF-CGL-001
 */
@Entity
@Table(name = "historial_configuracion_plataforma")
public class HistorialConfiguracionPlataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "configuracion_id", nullable = false)
    private Long configuracionId;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_nuevo", nullable = false, columnDefinition = "TEXT")
    private String valorNuevo;

    @Column(name = "cambiado_por")
    private Long cambiadoPor;

    @Column(name = "razon_cambio", length = 500)
    private String razonCambio;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    // ── Lifecycle Callbacks ──

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConfiguracionId() { return configuracionId; }
    public void setConfiguracionId(Long configuracionId) { this.configuracionId = configuracionId; }

    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }

    public String getValorNuevo() { return valorNuevo; }
    public void setValorNuevo(String valorNuevo) { this.valorNuevo = valorNuevo; }

    public Long getCambiadoPor() { return cambiadoPor; }
    public void setCambiadoPor(Long cambiadoPor) { this.cambiadoPor = cambiadoPor; }

    public String getRazonCambio() { return razonCambio; }
    public void setRazonCambio(String razonCambio) { this.razonCambio = razonCambio; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
