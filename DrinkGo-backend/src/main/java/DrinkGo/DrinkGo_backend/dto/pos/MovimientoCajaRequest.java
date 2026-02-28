package DrinkGo.DrinkGo_backend.dto.pos;

import java.math.BigDecimal;

/**
 * Request para registrar un movimiento manual de caja (ingreso/egreso).
 */
public class MovimientoCajaRequest {

    private Long sesionCajaId;
    private String tipoMovimiento; // ingreso_otro, egreso_gasto, egreso_otro
    private BigDecimal monto;
    private String descripcion;

    public MovimientoCajaRequest() {}

    public Long getSesionCajaId() { return sesionCajaId; }
    public void setSesionCajaId(Long sesionCajaId) { this.sesionCajaId = sesionCajaId; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
