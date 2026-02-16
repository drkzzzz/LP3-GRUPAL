package DrinkGo.DrinkGo_backend.dto;

import DrinkGo.DrinkGo_backend.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PagoPedidoDTO {
    
    private Long id;
    private PaymentMethod metodoPago;
    private BigDecimal monto;
    private String referencia;
    private String gateway;
    private String gatewayId;
    private OffsetDateTime pagadoEn;
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
