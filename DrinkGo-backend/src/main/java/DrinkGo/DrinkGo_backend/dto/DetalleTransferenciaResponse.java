package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO de respuesta para cada ítem de detalle de transferencia.
 */
public class DetalleTransferenciaResponse {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private Long loteId;
    private String numeroLote;
    private Integer cantidadSolicitada;
    private Integer cantidadEnviada;
    private Integer cantidadRecibida;
    private String notas;

    public DetalleTransferenciaResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public Integer getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(Integer cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }

    public Integer getCantidadEnviada() { return cantidadEnviada; }
    public void setCantidadEnviada(Integer cantidadEnviada) { this.cantidadEnviada = cantidadEnviada; }

    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Integer cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
