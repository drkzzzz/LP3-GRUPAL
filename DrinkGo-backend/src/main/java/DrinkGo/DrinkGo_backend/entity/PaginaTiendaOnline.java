package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entidad PaginaTiendaOnline - Páginas personalizadas de la tienda online
 * Tabla: paginas_tienda_online
 * BLOQUE 14: TIENDA ONLINE (STOREFRONT)
 */
@Entity
@Table(name = "paginas_tienda_online", schema = "drinkgo_db", uniqueConstraints = @UniqueConstraint(columnNames = {
        "negocio_id", "slug" }))
public class PaginaTiendaOnline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "slug", nullable = false, length = 200)
    private String slug;

    @Column(name = "contenido", columnDefinition = "LONGTEXT")
    private String contenido;

    @Column(name = "esta_publicado", nullable = false)
    private Boolean estaPublicado = false;

    @Column(name = "orden", nullable = false)
    private Integer orden = 0;

    @Column(name = "meta_titulo", length = 200)
    private String metaTitulo;

    @Column(name = "meta_descripcion", length = 500)
    private String metaDescripcion;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.creadoEn = OffsetDateTime.now();
        this.actualizadoEn = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = OffsetDateTime.now();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
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

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public OffsetDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(OffsetDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
