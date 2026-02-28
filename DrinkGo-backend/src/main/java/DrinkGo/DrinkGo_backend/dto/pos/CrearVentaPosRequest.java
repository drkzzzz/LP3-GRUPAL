package DrinkGo.DrinkGo_backend.dto.pos;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request para crear una venta desde el POS.
 * Contiene los items del carrito y los pagos.
 */
public class CrearVentaPosRequest {

    private Long negocioId;
    private Long sedeId;
    private Long sesionCajaId;
    private Long usuarioId;
    private Long clienteId;
    private BigDecimal descuentoGlobal;
    private String razonDescuento;
    private String tipoComprobante; // boleta, factura, nota_venta
    private String docClienteNumero;
    private String docClienteNombre;
    private List<ItemVentaPosDTO> items;
    private List<PagoVentaPosDTO> pagos;

    public CrearVentaPosRequest() {}

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
    public List<ItemVentaPosDTO> getItems() { return items; }
    public void setItems(List<ItemVentaPosDTO> items) { this.items = items; }
    public List<PagoVentaPosDTO> getPagos() { return pagos; }
    public void setPagos(List<PagoVentaPosDTO> pagos) { this.pagos = pagos; }
}
