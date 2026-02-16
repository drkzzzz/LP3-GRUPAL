package DrinkGo.DrinkGo_backend.dto;

public class PaginaTiendaOnlineDTO {
    
    private Long id;
    private Long negocioId;
    private String titulo;
    private String slug;
    private String contenido;
    private Boolean estaPublicado;
    private Integer orden;
    private String metaTitulo;
    private String metaDescripcion;
    
    public PaginaTiendaOnlineDTO() {}
    
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
}
