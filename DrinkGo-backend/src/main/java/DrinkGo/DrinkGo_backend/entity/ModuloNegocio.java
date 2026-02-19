package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad ModuloNegocio - Módulos activos por negocio según plan de suscripción.
 * Conecta los negocios con los módulos del sistema que tienen habilitados.
 */
@Entity
@Table(name = "modulos_negocio", 
       uniqueConstraints = @UniqueConstraint(
           name = "uk_modneg_negocio_modulo",
           columnNames = {"negocio_id", "modulo_id"}
       ))
public class ModuloNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_modneg_negocio"))
    private Negocio negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_modneg_modulo"))
    private ModuloSistema modulo;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "activado_en", nullable = false, updatable = false)
    private LocalDateTime activadoEn;

    @Column(name = "desactivado_en")
    private LocalDateTime desactivadoEn;

    // ── Lifecycle Callbacks ──

    @PrePersist
    protected void onCreate() {
        if (activadoEn == null) {
            activadoEn = LocalDateTime.now();
        }
    }

    // ── Getters y Setters ──

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Negocio getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
    }

    public ModuloSistema getModulo() {
        return modulo;
    }

    public void setModulo(ModuloSistema modulo) {
        this.modulo = modulo;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public LocalDateTime getActivadoEn() {
        return activadoEn;
    }

    public void setActivadoEn(LocalDateTime activadoEn) {
        this.activadoEn = activadoEn;
    }

    public LocalDateTime getDesactivadoEn() {
        return desactivadoEn;
    }

    public void setDesactivadoEn(LocalDateTime desactivadoEn) {
        this.desactivadoEn = desactivadoEn;
    }

    // ── Métodos de utilidad ──

    /**
     * Activa el módulo para el negocio
     */
    public void activar() {
        this.estaActivo = true;
        this.desactivadoEn = null;
    }

    /**
     * Desactiva el módulo para el negocio
     */
    public void desactivar() {
        this.estaActivo = false;
        this.desactivadoEn = LocalDateTime.now();
    }
}
