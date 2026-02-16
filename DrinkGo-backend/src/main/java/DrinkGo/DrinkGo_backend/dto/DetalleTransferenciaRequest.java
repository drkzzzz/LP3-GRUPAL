package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para cada ítem en una transferencia de inventario.
 */
public class DetalleTransferenciaRequest {

    private Long productoId;
    private Long loteId;
    private Integer cantidadSolicitada;

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
    }

    public Integer getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public void setCantidadSolicitada(Integer cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }
}
