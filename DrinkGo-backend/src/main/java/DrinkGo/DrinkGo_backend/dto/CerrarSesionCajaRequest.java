package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

/**
 * DTO de request para cerrar sesión de caja.
 * Bloque 8 - Ventas, POS y Cajas.
 */
public class CerrarSesionCajaRequest {

    private BigDecimal montoFinal;
    private String observacionesCierre;

    public CerrarSesionCajaRequest() {
    }

    // --- Getters y Setters ---

    public BigDecimal getMontoFinal() {
        return montoFinal;
    }

    public void setMontoFinal(BigDecimal montoFinal) {
        this.montoFinal = montoFinal;
    }

    public String getObservacionesCierre() {
        return observacionesCierre;
    }

    public void setObservacionesCierre(String observacionesCierre) {
        this.observacionesCierre = observacionesCierre;
    }
}
