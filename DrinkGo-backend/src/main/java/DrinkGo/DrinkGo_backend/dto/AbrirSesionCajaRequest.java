package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

/**
 * DTO de request para abrir sesión de caja.
 * Bloque 8 - Ventas, POS y Cajas.
 */
public class AbrirSesionCajaRequest {

    private Long cajaId;
    private BigDecimal montoInicial;
    private String observacionesApertura;

    public AbrirSesionCajaRequest() {
    }

    // --- Getters y Setters ---

    public Long getCajaId() {
        return cajaId;
    }

    public void setCajaId(Long cajaId) {
        this.cajaId = cajaId;
    }

    public BigDecimal getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(BigDecimal montoInicial) {
        this.montoInicial = montoInicial;
    }

    public String getObservacionesApertura() {
        return observacionesApertura;
    }

    public void setObservacionesApertura(String observacionesApertura) {
        this.observacionesApertura = observacionesApertura;
    }
}
