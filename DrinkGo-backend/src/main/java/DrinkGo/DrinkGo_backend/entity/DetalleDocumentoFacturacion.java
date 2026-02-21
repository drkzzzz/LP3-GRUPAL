package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entity para detalle de documentos de facturación electrónica.
 * Tabla: detalle_documentos_facturacion
 * RF-FACT-001: Items del documento de facturación
 */
@Entity
@Table(name = "detalle_documentos_facturacion")
public class DetalleDocumentoFacturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "documento_id", nullable = false)
    private Long documentoId;

    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "numero_item", nullable = false)
    private Integer numeroItem;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @Column(name = "codigo_unidad", nullable = false, length = 10)
    private String codigoUnidad = "NIU";

    @Column(name = "cantidad", precision = 12, scale = 4, nullable = false)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", precision = 10, scale = 4, nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "monto_descuento", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoDescuento = BigDecimal.ZERO;

    @Column(name = "monto_gravado", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoGravado;

    @Column(name = "monto_igv", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoIgv = BigDecimal.ZERO;

    @Column(name = "total", precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    // --- Constructores ---

    public DetalleDocumentoFacturacion() {}

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
