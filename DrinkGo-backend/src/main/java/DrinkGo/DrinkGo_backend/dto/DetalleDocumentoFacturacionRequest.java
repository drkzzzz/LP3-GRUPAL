package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

/**
 * DTO para items dentro del request de creación de documento de facturación.
 * Sin campo montoIsc (no existe en la tabla detalle_documentos_facturacion).
 */
public class DetalleDocumentoFacturacionRequest {

    private Long productoId;
    private String descripcion;
    private String codigoUnidad; // NIU por defecto (Código SUNAT para unidades)
    private BigDecimal cantidad;
    private BigDecimal precioUnitario; // Precio SIN IGV (base imponible por unidad)
    private BigDecimal montoDescuento;

    // --- Getters y Setters ---

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCodigoUnidad() { return codigoUnidad; }
    public void setCodigoUnidad(String codigoUnidad) { this.codigoUnidad = codigoUnidad; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getMontoDescuento() { return montoDescuento; }
    public void setMontoDescuento(BigDecimal montoDescuento) { this.montoDescuento = montoDescuento; }
}
