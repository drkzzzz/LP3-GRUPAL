package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagosPedidoDTO {

    private Long id;
    private Long pedidoId;
    private Long metodoPagoId;
    private BigDecimal monto;
    private String referencia;
    private LocalDateTime creadoEn;

    // Constructores
    public PagosPedidoDTO() {
    }

    public PagosPedidoDTO(Long id, Long pedidoId, Long metodoPagoId, BigDecimal monto, String referencia,
            LocalDateTime creadoEn) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.metodoPagoId = metodoPagoId;
        this.monto = monto;
        this.referencia = referencia;
        this.creadoEn = creadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getMetodoPagoId() {
        return metodoPagoId;
    }

    public void setMetodoPagoId(Long metodoPagoId) {
        this.metodoPagoId = metodoPagoId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
