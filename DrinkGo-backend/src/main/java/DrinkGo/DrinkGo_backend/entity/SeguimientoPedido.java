package DrinkGo.DrinkGo_backend.entity;

import DrinkGo.DrinkGo_backend.enums.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entidad SeguimientoPedido - Historial de estados y tracking de pedidos
 * Tabla: drinkgo.seguimiento_pedido
 */
@Entity
@Table(name = "seguimiento_pedido", schema = "drinkgo")
public class SeguimientoPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private OrderStatus estadoAnterior;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false)
    private OrderStatus estadoNuevo;
    
    @Column(name = "mensaje", length = 250)
    private String mensaje;
    
    @Column(name = "ubicacion_lat", precision = 10, scale = 7)
    private BigDecimal ubicacionLat;
    
    @Column(name = "ubicacion_lng", precision = 10, scale = 7)
    private BigDecimal ubicacionLng;
    
    @Column(name = "usuario_id")
    private Long usuarioId;
    
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
    
    public OrderStatus getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(OrderStatus estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    
    public OrderStatus getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(OrderStatus estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public BigDecimal getUbicacionLat() { return ubicacionLat; }
    public void setUbicacionLat(BigDecimal ubicacionLat) { this.ubicacionLat = ubicacionLat; }
    
    public BigDecimal getUbicacionLng() { return ubicacionLng; }
    public void setUbicacionLng(BigDecimal ubicacionLng) { this.ubicacionLng = ubicacionLng; }
    
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    
    public OffsetDateTime getCreadoEn() { return creadoEn; }
}
