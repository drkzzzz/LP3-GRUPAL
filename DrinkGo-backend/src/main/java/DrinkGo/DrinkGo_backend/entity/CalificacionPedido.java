package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entidad CalificacionPedido - Calificaciones de clientes sobre pedidos
 * Tabla: drinkgo.calificacion_pedido
 */
@Entity
@Table(name = "calificacion_pedido", schema = "drinkgo")
public class CalificacionPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pedido_id", unique = true, nullable = false)
    private Long pedidoId;
    
    @Column(name = "estrellas", nullable = false)
    private Integer estrellas; // 1-5
    
    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;
    
    @Column(name = "puntualidad")
    private Integer puntualidad; // 1-5
    
    @Column(name = "calidad_producto")
    private Integer calidadProducto; // 1-5
    
    @Column(name = "atencion")
    private Integer atencion; // 1-5
    
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
    
    @PrePersist
    protected void onCreate() {
        creadoEn = OffsetDateTime.now();
    }
    
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
}
