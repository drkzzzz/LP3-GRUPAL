package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

/**
 * DTO para un item dentro de una orden de compra.
 * Bloque 6. Usado en OrdenCompraCreateRequest y OrdenCompraUpdateRequest.
 */
public class DetalleOrdenCompraRequest {

    private Long productoId;
    private Integer cantidadOrdenada;
    private BigDecimal precioUnitario;
    private BigDecimal tasaImpuesto;
    private BigDecimal porcentajeDescuento;
    private String notas;

    // --- Getters y Setters ---

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getCantidadOrdenada() { return cantidadOrdenada; }
    public void setCantidadOrdenada(Integer cantidadOrdenada) { this.cantidadOrdenada = cantidadOrdenada; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getTasaImpuesto() { return tasaImpuesto; }
    public void setTasaImpuesto(BigDecimal tasaImpuesto) { this.tasaImpuesto = tasaImpuesto; }

    public BigDecimal getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}