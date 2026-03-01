package DrinkGo.DrinkGo_backend.dto.pos;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para crear una venta desde el POS.
 */
public class CrearVentaPosRequest {

    private Long negocioId;
    private Long sedeId;
    private Long sesionCajaId;
    private Long usuarioId;
    private Long clienteId;
    private BigDecimal descuentoGlobal;
    private String razonDescuento;
    private String tipoComprobante; // boleta | factura | nota_venta
    private String docClienteNumero;
    private String docClienteNombre;
    private List<ItemVenta> items;
    private List<PagoVenta> pagos;

    public static class ItemVenta {
        private Long productoId;
        private String nombreProducto;
        private BigDecimal precioUnitario;
        private Integer cantidad;
        private BigDecimal descuento;

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public BigDecimal getDescuento() { return descuento; }
        public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    }

    public static class PagoVenta {
        private Long metodoPagoId;
        private BigDecimal monto;
        private String numeroReferencia;

        public Long getMetodoPagoId() { return metodoPagoId; }
        public void setMetodoPagoId(Long metodoPagoId) { this.metodoPagoId = metodoPagoId; }
        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }
        public String getNumeroReferencia() { return numeroReferencia; }
        public void setNumeroReferencia(String numeroReferencia) { this.numeroReferencia = numeroReferencia; }
    }

    // Getters y Setters
    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
    public Long getSesionCajaId() { return sesionCajaId; }
    public void setSesionCajaId(Long sesionCajaId) { this.sesionCajaId = sesionCajaId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public BigDecimal getDescuentoGlobal() { return descuentoGlobal; }
    public void setDescuentoGlobal(BigDecimal descuentoGlobal) { this.descuentoGlobal = descuentoGlobal; }
    public String getRazonDescuento() { return razonDescuento; }
    public void setRazonDescuento(String razonDescuento) { this.razonDescuento = razonDescuento; }
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    public String getDocClienteNumero() { return docClienteNumero; }
    public void setDocClienteNumero(String docClienteNumero) { this.docClienteNumero = docClienteNumero; }
    public String getDocClienteNombre() { return docClienteNombre; }
    public void setDocClienteNombre(String docClienteNombre) { this.docClienteNombre = docClienteNombre; }
    public List<ItemVenta> getItems() { return items; }
    public void setItems(List<ItemVenta> items) { this.items = items; }
    public List<PagoVenta> getPagos() { return pagos; }
    public void setPagos(List<PagoVenta> pagos) { this.pagos = pagos; }
}
