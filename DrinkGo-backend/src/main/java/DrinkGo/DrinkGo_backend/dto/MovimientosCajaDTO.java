package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientosCajaDTO {

    private Long id;
    private Long sesionCajaId;
    private String tipoMovimiento;
    private BigDecimal monto;
    private String concepto;
    private String referencia;
    private LocalDateTime creadoEn;

    // Constructores
    public MovimientosCajaDTO() {
    }

    public MovimientosCajaDTO(Long id, Long sesionCajaId, String tipoMovimiento, BigDecimal monto, String concepto,
            String referencia, LocalDateTime creadoEn) {
        this.id = id;
        this.sesionCajaId = sesionCajaId;
        this.tipoMovimiento = tipoMovimiento;
        this.monto = monto;
        this.concepto = concepto;
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

    public Long getSesionCajaId() {
        return sesionCajaId;
    }

    public void setSesionCajaId(Long sesionCajaId) {
        this.sesionCajaId = sesionCajaId;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
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
