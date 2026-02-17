package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO de request para crear/actualizar cajas registradoras.
 * Bloque 8 - Ventas, POS y Cajas.
 */
public class CajaRegistradoraRequest {

    private Long sedeId;
    private String nombreCaja;
    private String codigoCaja;
    private Boolean estaActivo;

    public CajaRegistradoraRequest() {
    }

    // --- Getters y Setters ---

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
    }

    public String getNombreCaja() {
        return nombreCaja;
    }

    public void setNombreCaja(String nombreCaja) {
        this.nombreCaja = nombreCaja;
    }

    public String getCodigoCaja() {
        return codigoCaja;
    }

    public void setCodigoCaja(String codigoCaja) {
        this.codigoCaja = codigoCaja;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }
}
