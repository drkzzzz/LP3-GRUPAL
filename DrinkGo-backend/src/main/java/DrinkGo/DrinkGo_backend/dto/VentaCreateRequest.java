package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de request para crear ventas.
 * Bloque 8 - Ventas, POS y Cajas.
 */
public class VentaCreateRequest {

    private Long sedeId;
    private Long sesionId;
    private Long clienteId;
    private String tipoVenta; // LOCAL, DELIVERY, PICK_UP, MARKETPLACE
    private String canalVenta;
    private Long mesaId;
    private BigDecimal descuentoMonto;
    private BigDecimal costoDelivery;
    private BigDecimal propina;
    private String tipoComprobante;
    private String observaciones;
    private String direccionEntrega;
    private String telefonoEntrega;
    private String nombreClienteManual;

    private List<DetalleVentaItemRequest> detalles;
    private List<PagoVentaItemRequest> pagos;

    public VentaCreateRequest() {
    }

    // --- Inner Classes ---

    public static class DetalleVentaItemRequest {
        private Long productoId;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuentoPorItem;
        private String observaciones;

        // Getters y Setters
        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public BigDecimal getDescuentoPorItem() {
            return descuentoPorItem;
        }

        public void setDescuentoPorItem(BigDecimal descuentoPorItem) {
            this.descuentoPorItem = descuentoPorItem;
        }

        public String getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }
    }

    public static class PagoVentaItemRequest {
        private Long metodoPagoId;
        private BigDecimal monto;
        private String referenciaPago;

        // Getters y Setters
        public Long getMetodoPagoId() {
            return metodoPagoId;
        }

        public void setMetodoPagoId(Long metodoPagoId) {
            this.metodoPagoId = metodoPagoId;
        }

        public BigDecimal getMonto() {
            return monto;
        }

        public void setMonto(BigDecimal monto) {
            this.monto = monto;
        }

        public String getReferenciaPago() {
            return referenciaPago;
        }

        public void setReferenciaPago(String referenciaPago) {
            this.referenciaPago = referenciaPago;
        }
    }

    // --- Getters y Setters ---

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
    }

    public Long getSesionId() {
        return sesionId;
    }

    public void setSesionId(Long sesionId) {
        this.sesionId = sesionId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public String getCanalVenta() {
        return canalVenta;
    }

    public void setCanalVenta(String canalVenta) {
        this.canalVenta = canalVenta;
    }

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }

    public BigDecimal getDescuentoMonto() {
        return descuentoMonto;
    }

    public void setDescuentoMonto(BigDecimal descuentoMonto) {
        this.descuentoMonto = descuentoMonto;
    }

    public BigDecimal getCostoDelivery() {
        return costoDelivery;
    }

    public void setCostoDelivery(BigDecimal costoDelivery) {
        this.costoDelivery = costoDelivery;
    }

    public BigDecimal getPropina() {
        return propina;
    }

    public void setPropina(BigDecimal propina) {
        this.propina = propina;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getTelefonoEntrega() {
        return telefonoEntrega;
    }

    public void setTelefonoEntrega(String telefonoEntrega) {
        this.telefonoEntrega = telefonoEntrega;
    }

    public String getNombreClienteManual() {
        return nombreClienteManual;
    }

    public void setNombreClienteManual(String nombreClienteManual) {
        this.nombreClienteManual = nombreClienteManual;
    }

    public List<DetalleVentaItemRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaItemRequest> detalles) {
        this.detalles = detalles;
    }

    public List<PagoVentaItemRequest> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoVentaItemRequest> pagos) {
        this.pagos = pagos;
    }
}
