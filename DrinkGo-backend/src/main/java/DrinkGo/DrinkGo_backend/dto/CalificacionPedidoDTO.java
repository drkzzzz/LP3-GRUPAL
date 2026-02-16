package DrinkGo.DrinkGo_backend.dto;

import java.time.OffsetDateTime;

public class CalificacionPedidoDTO {
    
    private Long id;
    private Long pedidoId;
    private Integer estrellas;
    private String comentario;
    private Integer puntualidad;
    private Integer calidadProducto;
    private Integer atencion;
    private OffsetDateTime creadoEn;
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    
    public Integer getEstrellas() { return estrellas; }
    public void setEstrellas(Integer estrellas) { this.estrellas = estrellas; }
    
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    
    public Integer getPuntualidad() { return puntualidad; }
    public void setPuntualidad(Integer puntualidad) { this.puntualidad = puntualidad; }
    
    public Integer getCalidadProducto() { return calidadProducto; }
    public void setCalidadProducto(Integer calidadProducto) { this.calidadProducto = calidadProducto; }
    
    public Integer getAtencion() { return atencion; }
    public void setAtencion(Integer atencion) { this.atencion = atencion; }
    
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
}
