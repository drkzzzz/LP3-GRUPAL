package DrinkGo.DrinkGo_backend.dto.pos;

import java.math.BigDecimal;

/**
 * Request para cerrar una sesión de caja (arqueo).
 */
public class CerrarCajaRequest {

    private Long sesionCajaId;
    private BigDecimal montoContado;
    private String observaciones;

    public CerrarCajaRequest() {}

    public Long getSesionCajaId() { return sesionCajaId; }
    public void setSesionCajaId(Long sesionCajaId) { this.sesionCajaId = sesionCajaId; }
    public BigDecimal getMontoContado() { return montoContado; }
    public void setMontoContado(BigDecimal montoContado) { this.montoContado = montoContado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
