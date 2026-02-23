package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class PaginasTiendaOnlineDTO {

    private Long id;
    private Long negocioId;
    private String titulo;
    private String slug;
    private String contenido;
    private Boolean estaPublicada;
    private Integer orden;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public PaginasTiendaOnlineDTO() {
    }

    public PaginasTiendaOnlineDTO(Long id, Long negocioId, String titulo, String slug, String contenido,
            Boolean estaPublicada, Integer orden, Boolean estaActivo, LocalDateTime creadoEn,
            LocalDateTime actualizadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.titulo = titulo;
        this.slug = slug;
        this.contenido = contenido;
        this.estaPublicada = estaPublicada;
        this.orden = orden;
        this.estaActivo = estaActivo;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
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

    public Boolean getEstaPublicada() {
        return estaPublicada;
    }

    public void setEstaPublicada(Boolean estaPublicada) {
        this.estaPublicada = estaPublicada;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
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
}
