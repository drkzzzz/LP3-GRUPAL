package DrinkGo.DrinkGo_backend.dto.pos;

import java.math.BigDecimal;

/**
 * DTO para cerrar una sesión de caja.
 */
public class CerrarCajaRequest {

    private Long sesionCajaId;
    private Long usuarioId;
    private BigDecimal montoCierre;
    private BigDecimal totalEfectivo;
    private BigDecimal totalTarjeta;
    private BigDecimal totalYape;
    private BigDecimal totalPlin;
    private BigDecimal totalOtros;
    private String observaciones;

    public Long getSesionCajaId() { return sesionCajaId; }
    public void setSesionCajaId(Long sesionCajaId) { this.sesionCajaId = sesionCajaId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public BigDecimal getMontoCierre() { return montoCierre; }
    public void setMontoCierre(BigDecimal montoCierre) { this.montoCierre = montoCierre; }
    public BigDecimal getTotalEfectivo() { return totalEfectivo; }
    public void setTotalEfectivo(BigDecimal totalEfectivo) { this.totalEfectivo = totalEfectivo; }
    public BigDecimal getTotalTarjeta() { return totalTarjeta; }
    public void setTotalTarjeta(BigDecimal totalTarjeta) { this.totalTarjeta = totalTarjeta; }
    public BigDecimal getTotalYape() { return totalYape; }
    public void setTotalYape(BigDecimal totalYape) { this.totalYape = totalYape; }
    public BigDecimal getTotalPlin() { return totalPlin; }
    public void setTotalPlin(BigDecimal totalPlin) { this.totalPlin = totalPlin; }
    public BigDecimal getTotalOtros() { return totalOtros; }
    public void setTotalOtros(BigDecimal totalOtros) { this.totalOtros = totalOtros; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
