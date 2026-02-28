package DrinkGo.DrinkGo_backend.dto.pos;

import java.math.BigDecimal;

/**
 * Request para abrir una sesión de caja (turno).
 */
public class AbrirCajaRequest {

    private Long cajaId;
    private Long usuarioId;
    private Long negocioId;
    private BigDecimal montoApertura;

    public AbrirCajaRequest() {}

    public Long getCajaId() { return cajaId; }
    public void setCajaId(Long cajaId) { this.cajaId = cajaId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }
    public BigDecimal getMontoApertura() { return montoApertura; }
    public void setMontoApertura(BigDecimal montoApertura) { this.montoApertura = montoApertura; }
}
