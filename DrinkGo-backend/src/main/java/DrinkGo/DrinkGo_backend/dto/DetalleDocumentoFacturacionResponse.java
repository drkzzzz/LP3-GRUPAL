package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

/**
 * Response para items del documento de facturación.
 * Sin campo montoIsc (no existe en la tabla detalle_documentos_facturacion).
 */
public class DetalleDocumentoFacturacionResponse {

    private Long id;
    private Long documentoId;
    private Long productoId;
    private Integer numeroItem;
    private String descripcion;
    private String codigoUnidad;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal montoDescuento;
    private BigDecimal montoGravado;
    private BigDecimal montoIgv;
    private BigDecimal total;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDocumentoId() { return documentoId; }
    public void setDocumentoId(Long documentoId) { this.documentoId = documentoId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getNumeroItem() { return numeroItem; }
    public void setNumeroItem(Integer numeroItem) { this.numeroItem = numeroItem; }

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

    public BigDecimal getMontoGravado() { return montoGravado; }
    public void setMontoGravado(BigDecimal montoGravado) { this.montoGravado = montoGravado; }

    public BigDecimal getMontoIgv() { return montoIgv; }
    public void setMontoIgv(BigDecimal montoIgv) { this.montoIgv = montoIgv; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
