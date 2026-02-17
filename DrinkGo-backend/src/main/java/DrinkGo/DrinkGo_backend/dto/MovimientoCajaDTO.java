package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para movimientos de caja.
 * Bloque 8 - Ventas, POS y Cajas.
 */
public class MovimientoCajaDTO {

    private Long id;
    private Long negocioId;
    private Long sesionId;
    private String tipoMovimiento; // INGRESO, EGRESO, AJUSTE
    private BigDecimal monto;
    private String concepto;
    private Long realizadoPorUsuarioId;
    private String realizadoPorUsuarioNombre;
    private LocalDateTime creadoEn;

    public MovimientoCajaDTO() {
    }

    // --- Getters y Setters ---

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

    public Long getSesionId() {
        return sesionId;
    }

    public void setSesionId(Long sesionId) {
        this.sesionId = sesionId;
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

    public Long getRealizadoPorUsuarioId() {
        return realizadoPorUsuarioId;
    }

    public void setRealizadoPorUsuarioId(Long realizadoPorUsuarioId) {
        this.realizadoPorUsuarioId = realizadoPorUsuarioId;
    }

    public String getRealizadoPorUsuarioNombre() {
        return realizadoPorUsuarioNombre;
    }

    public void setRealizadoPorUsuarioNombre(String realizadoPorUsuarioNombre) {
        this.realizadoPorUsuarioNombre = realizadoPorUsuarioNombre;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
