package DrinkGo.DrinkGo_backend.dto.pos;

import java.math.BigDecimal;

/**
 * DTO para abrir una sesión de caja.
 */
public class AbrirCajaRequest {

    private Long cajaId;
    private Long usuarioId;
    private Long negocioId;
    private BigDecimal montoApertura;

    public Long getCajaId() { return cajaId; }
    public void setCajaId(Long cajaId) { this.cajaId = cajaId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }
    public BigDecimal getMontoApertura() { return montoApertura; }
    public void setMontoApertura(BigDecimal montoApertura) { this.montoApertura = montoApertura; }
}
