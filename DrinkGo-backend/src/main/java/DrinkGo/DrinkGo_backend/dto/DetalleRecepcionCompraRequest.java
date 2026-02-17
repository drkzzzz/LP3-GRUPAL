package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO Request para DetalleRecepcionCompra
 */
public class DetalleRecepcionCompraRequest {

    private Long recepcionId;
    private Long detalleOrdenCompraId;
    private Long productoId;
    private Long loteId;
    private Integer cantidadRecibida;
    private Integer cantidadRechazada;
    private String razonRechazo;

    // ── Getters y Setters ──

    public Long getRecepcionId() { return recepcionId; }
    public void setRecepcionId(Long recepcionId) { this.recepcionId = recepcionId; }

    public Long getDetalleOrdenCompraId() { return detalleOrdenCompraId; }
    public void setDetalleOrdenCompraId(Long detalleOrdenCompraId) { this.detalleOrdenCompraId = detalleOrdenCompraId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }

    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Integer cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    public Integer getCantidadRechazada() { return cantidadRechazada; }
    public void setCantidadRechazada(Integer cantidadRechazada) { this.cantidadRechazada = cantidadRechazada; }

    public String getRazonRechazo() { return razonRechazo; }
    public void setRazonRechazo(String razonRechazo) { this.razonRechazo = razonRechazo; }
}
