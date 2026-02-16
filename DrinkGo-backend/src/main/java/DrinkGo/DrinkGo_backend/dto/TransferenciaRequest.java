package DrinkGo.DrinkGo_backend.dto;

import java.util.List;

/**
 * DTO para crear una transferencia entre almacenes (RF-INV-007).
 */
public class TransferenciaRequest {

    private Long almacenOrigenId;
    private Long almacenDestinoId;
    private String notas;
    private List<DetalleTransferenciaRequest> detalles;

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

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<DetalleTransferenciaRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleTransferenciaRequest> detalles) {
        this.detalles = detalles;
    }
}
