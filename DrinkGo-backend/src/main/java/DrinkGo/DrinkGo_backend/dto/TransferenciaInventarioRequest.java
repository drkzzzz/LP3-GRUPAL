package DrinkGo.DrinkGo_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

/**
 * DTO para crear o actualizar una transferencia de inventario.
 * negocio_id y solicitado_por se obtienen del token JWT.
 */
public class TransferenciaInventarioRequest {

    @NotNull(message = "El almacen_origen_id es obligatorio")
    private Long almacenOrigenId;

    @NotNull(message = "El almacen_destino_id es obligatorio")
    private Long almacenDestinoId;

    @Size(max = 65535, message = "Las notas son demasiado largas")
    private String notas;

    @NotEmpty(message = "Debe incluir al menos un detalle de transferencia")
    @Valid
    private List<DetalleTransferenciaRequest> detalles;

    public TransferenciaInventarioRequest() {}

    public Long getAlmacenOrigenId() { return almacenOrigenId; }
    public void setAlmacenOrigenId(Long almacenOrigenId) { this.almacenOrigenId = almacenOrigenId; }

    public Long getAlmacenDestinoId() { return almacenDestinoId; }
    public void setAlmacenDestinoId(Long almacenDestinoId) { this.almacenDestinoId = almacenDestinoId; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public List<DetalleTransferenciaRequest> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleTransferenciaRequest> detalles) { this.detalles = detalles; }
}
