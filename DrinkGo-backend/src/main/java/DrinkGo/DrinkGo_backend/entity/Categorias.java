package DrinkGo.DrinkGo_backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "categorias")
@SQLDelete(sql = "UPDATE categorias SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "nombre", "slug", "descripcion",
        "esAlcoholica", "visibleTiendaOnline", "estaActivo", "creadoEn", "actualizadoEn" })
public class Categorias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "es_alcoholica")
    private Boolean esAlcoholica = false;

    @Column(name = "visible_tienda_online")
    private Boolean visibleTiendaOnline = true;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEsAlcoholica() {
        return esAlcoholica;
    }

    public void setEsAlcoholica(Boolean esAlcoholica) {
        this.esAlcoholica = esAlcoholica;
    }

    public Boolean getVisibleTiendaOnline() {
        return visibleTiendaOnline;
    }

    public void setVisibleTiendaOnline(Boolean visibleTiendaOnline) {
        this.visibleTiendaOnline = visibleTiendaOnline;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
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
        return "Categorias [id=" + id + ", negocio=" + (negocio != null ? negocio.getId() : null)
                + ", nombre=" + nombre + ", slug=" + slug + ", descripcion=" + descripcion
                + ", esAlcoholica=" + esAlcoholica + ", visibleTiendaOnline=" + visibleTiendaOnline + ", estaActivo=" + estaActivo
                + ", creadoEn=" + creadoEn + ", actualizadoEn=" + actualizadoEn + "]";
    }
}
