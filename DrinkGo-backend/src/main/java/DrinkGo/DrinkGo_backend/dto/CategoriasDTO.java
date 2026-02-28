package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class CategoriasDTO {

    private Long id;
    private Long negocioId;
    private String nombre;
    private String slug;
    private String descripcion;
    private Boolean esAlcoholica;
    private Boolean visibleTiendaOnline;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public CategoriasDTO() {
    }

    public CategoriasDTO(Long id, Long negocioId, String nombre, String slug, String descripcion,
            Boolean esAlcoholica, Boolean visibleTiendaOnline, Boolean estaActivo,
            LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.nombre = nombre;
        this.slug = slug;
        this.descripcion = descripcion;
        this.esAlcoholica = esAlcoholica;
        this.visibleTiendaOnline = visibleTiendaOnline;
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
}
