package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagosVentaDTO {

    private Long id;
    private Long ventaId;
    private Long metodoPagoId;
    private BigDecimal monto;
    private String referencia;
    private LocalDateTime creadoEn;

    // Constructores
    public PagosVentaDTO() {
    }

    public PagosVentaDTO(Long id, Long ventaId, Long metodoPagoId, BigDecimal monto, String referencia,
            LocalDateTime creadoEn) {
        this.id = id;
        this.ventaId = ventaId;
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

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
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
