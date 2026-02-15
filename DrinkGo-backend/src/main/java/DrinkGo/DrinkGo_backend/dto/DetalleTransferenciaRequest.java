package DrinkGo.DrinkGo_backend.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para cada ítem de detalle en una transferencia.
 */
public class DetalleTransferenciaRequest {

    @NotNull(message = "El producto_id es obligatorio")
    private Long productoId;

    private Long loteId;

    @NotNull(message = "La cantidad_solicitada es obligatoria")
    @Min(value = 1, message = "La cantidad_solicitada debe ser al menos 1")
    private Integer cantidadSolicitada;

    @Size(max = 300, message = "Las notas no pueden exceder 300 caracteres")
    private String notas;

    public DetalleTransferenciaRequest() {}

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }

    public Integer getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(Integer cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
