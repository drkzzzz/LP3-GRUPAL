package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_tienda_online")
@JsonPropertyOrder({ "id", "negocioId", "estaHabilitado", "nombreTienda", "slugTienda", "dominioPersonalizado",
        "configVisual", "configSeo", "configMarketing", "configReglas", "creadoEn", "actualizadoEn" })
public class ConfiguracionTiendaOnline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false, unique = true)
    private Negocios negocio;

    @Column(name = "esta_habilitado", nullable = false)
    private Boolean estaHabilitado = false;

    @Column(name = "nombre_tienda", length = 200)
    private String nombreTienda;

    @Column(name = "slug_tienda", length = 100, unique = true)
    private String slugTienda;

    @Column(name = "dominio_personalizado", length = 200)
    private String dominioPersonalizado;

    @Column(name = "config_visual", columnDefinition = "JSON")
    private String configVisual;

    @Column(name = "config_seo", columnDefinition = "JSON")
    private String configSeo;

    @Column(name = "config_marketing", columnDefinition = "JSON")
    private String configMarketing;

    @Column(name = "config_reglas", columnDefinition = "JSON")
    private String configReglas;

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

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Negocios getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocios negocio) {
        this.negocio = negocio;
    }

    public Boolean getEstaHabilitado() {
        return estaHabilitado;
    }

    public void setEstaHabilitado(Boolean estaHabilitado) {
        this.estaHabilitado = estaHabilitado;
    }

    public String getNombreTienda() {
        return nombreTienda;
    }

    public void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda;
    }

    public String getSlugTienda() {
        return slugTienda;
    }

    public void setSlugTienda(String slugTienda) {
        this.slugTienda = slugTienda;
    }

    public String getDominioPersonalizado() {
        return dominioPersonalizado;
    }

    public void setDominioPersonalizado(String dominioPersonalizado) {
        this.dominioPersonalizado = dominioPersonalizado;
    }

    public String getConfigVisual() {
        return configVisual;
    }

    public void setConfigVisual(String configVisual) {
        this.configVisual = configVisual;
    }

    public String getConfigSeo() {
        return configSeo;
    }

    public void setConfigSeo(String configSeo) {
        this.configSeo = configSeo;
    }

    public String getConfigMarketing() {
        return configMarketing;
    }

    public void setConfigMarketing(String configMarketing) {
        this.configMarketing = configMarketing;
    }

    public String getConfigReglas() {
        return configReglas;
    }

    public void setConfigReglas(String configReglas) {
        this.configReglas = configReglas;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    @Override
    public String toString() {
        return "ConfiguracionTiendaOnline [id=" + id + ", nombreTienda=" + nombreTienda + ", slugTienda=" + slugTienda
                + ", estaHabilitado=" + estaHabilitado + "]";
    }
}
