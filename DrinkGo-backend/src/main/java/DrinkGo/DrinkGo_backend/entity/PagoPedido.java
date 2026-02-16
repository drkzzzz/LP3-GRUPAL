package DrinkGo.DrinkGo_backend.entity;

import DrinkGo.DrinkGo_backend.enums.PaymentMethod;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entidad PagoPedido - Pagos de pedidos
 * Tabla: drinkgo.pago_pedido
 */
@Entity
@Table(name = "pago_pedido", schema = "drinkgo")
public class PagoPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private PaymentMethod metodoPago;
    
    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;
    
    @Column(name = "referencia", length = 120)
    private String referencia;
    
    @Column(name = "gateway", length = 50)
    private String gateway;
    
    @Column(name = "gateway_id", length = 120)
    private String gatewayId;
    
    @Column(name = "pagado_en", nullable = false)
    private OffsetDateTime pagadoEn;
    
    @PrePersist
    protected void onCreate() {
        pagadoEn = OffsetDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    
    public PaymentMethod getMetodoPago() { return metodoPago; }
    public void setMetodoPago(PaymentMethod metodoPago) { this.metodoPago = metodoPago; }
    
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    
    public String getGateway() { return gateway; }
    public void setGateway(String gateway) { this.gateway = gateway; }
    
    public String getGatewayId() { return gatewayId; }
    public void setGatewayId(String gatewayId) { this.gatewayId = gatewayId; }
    
    public OffsetDateTime getPagadoEn() { return pagadoEn; }
    public void setPagadoEn(OffsetDateTime pagadoEn) { this.pagadoEn = pagadoEn; }
}
