package DrinkGo.DrinkGo_backend.dto.facturacion;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para solicitar la creación de un comprobante desde Ventas.
 * Ventas envía los datos; Facturación genera el correlativo.
 */
public class CrearComprobanteRequest {

    private Long negocioId;
    private Long serieFacturacionId;
    private String tipoDocumento; // boleta | factura
    private Long clienteId;
    private Long ventaId;
    private Long usuarioId;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private List<DetalleComprobanteRequest> detalles;

    // ─── Inner class para detalles ───
    public static class DetalleComprobanteRequest {
        private Long productoId;
        private Long comboId;
        private BigDecimal cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuento;
        private BigDecimal subtotal;
        private BigDecimal impuesto;
        private BigDecimal total;

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public Long getComboId() { return comboId; }
        public void setComboId(Long comboId) { this.comboId = comboId; }
        public BigDecimal getCantidad() { return cantidad; }
        public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
        public BigDecimal getDescuento() { return descuento; }
        public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
        public BigDecimal getImpuesto() { return impuesto; }
        public void setImpuesto(BigDecimal impuesto) { this.impuesto = impuesto; }
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
    }

    // Getters y Setters
    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }
    public Long getSerieFacturacionId() { return serieFacturacionId; }
    public void setSerieFacturacionId(Long serieFacturacionId) { this.serieFacturacionId = serieFacturacionId; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getVentaId() { return ventaId; }
    public void setVentaId(Long ventaId) { this.ventaId = ventaId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getImpuestos() { return impuestos; }
    public void setImpuestos(BigDecimal impuestos) { this.impuestos = impuestos; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<DetalleComprobanteRequest> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleComprobanteRequest> detalles) { this.detalles = detalles; }
}
