package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

/**
 * DTO de request para crear movimientos de caja.
 * Bloque 8 - Ventas, POS y Cajas.
 */
public class MovimientoCajaRequest {

    private Long sesionId;
    private String tipoMovimiento; // INGRESO, EGRESO, AJUSTE
    private BigDecimal monto;
    private String concepto;

    public MovimientoCajaRequest() {
    }

    // --- Getters y Setters ---

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
}
