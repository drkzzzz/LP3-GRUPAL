package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "paginas_tienda_online")
@JsonPropertyOrder({ "id", "negocioId", "tipo", "titulo", "slug", "contenido", "estaPublicado", "orden", "metaTitulo",
        "metaDescripcion", "creadoEn", "actualizadoEn" })
public class PaginasTiendaOnline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPagina tipo = TipoPagina.contenido;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 200)
    private String slug;

    @Column(columnDefinition = "LONGTEXT")
    private String contenido;

    @Column(name = "esta_publicado", nullable = false)
    private Boolean estaPublicado = false;

    @Column(nullable = false)
    private Integer orden = 0;

    @Column(name = "meta_titulo", length = 200)
    private String metaTitulo;

    @Column(name = "meta_descripcion", length = 500)
    private String metaDescripcion;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    public enum TipoPagina {
        contenido, legal, sistema
    }

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

    public TipoPagina getTipo() {
        return tipo;
    }

    public void setTipo(TipoPagina tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Boolean getEstaPublicado() {
        return estaPublicado;
    }

    public void setEstaPublicado(Boolean estaPublicado) {
        this.estaPublicado = estaPublicado;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public String getMetaTitulo() {
        return metaTitulo;
    }

    public void setMetaTitulo(String metaTitulo) {
        this.metaTitulo = metaTitulo;
    }

    public String getMetaDescripcion() {
        return metaDescripcion;
    }

    public void setMetaDescripcion(String metaDescripcion) {
        this.metaDescripcion = metaDescripcion;
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
        return "PaginasTiendaOnline [id=" + id + ", tipo=" + tipo + ", titulo=" + titulo + ", slug=" + slug
                + ", estaPublicado=" + estaPublicado + "]";
    }
}
