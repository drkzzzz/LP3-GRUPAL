package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para una condición de promoción (sub-objeto dentro de PromocionCreateRequest/UpdateRequest).
 * Bloque 13.
 */
public class CondicionPromocionRequest {

    private String tipoEntidad;   // producto, categoria, marca
    private Long entidadId;

    // --- Getters y Setters ---

    public String getTipoEntidad() { return tipoEntidad; }
    public void setTipoEntidad(String tipoEntidad) { this.tipoEntidad = tipoEntidad; }

    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }
}