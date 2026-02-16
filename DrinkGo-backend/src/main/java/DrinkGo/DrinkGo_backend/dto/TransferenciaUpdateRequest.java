package DrinkGo.DrinkGo_backend.dto;

import java.util.List;

/**
 * DTO para actualizar una transferencia (PUT).
 * Solo se puede actualizar en estado 'borrador'.
 */
public class TransferenciaUpdateRequest {

    private String notas;
    private Long almacenOrigenId;
    private Long almacenDestinoId;
    private List<DetalleTransferenciaRequest> detalles;

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Long getAlmacenOrigenId() {
        return almacenOrigenId;
    }

    public void setAlmacenOrigenId(Long almacenOrigenId) {
        this.almacenOrigenId = almacenOrigenId;
    }

    public Long getAlmacenDestinoId() {
        return almacenDestinoId;
    }

    public void setAlmacenDestinoId(Long almacenDestinoId) {
        this.almacenDestinoId = almacenDestinoId;
    }

    public List<DetalleTransferenciaRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleTransferenciaRequest> detalles) {
        this.detalles = detalles;
    }
}
