package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad PedidoItem - Items de cada pedido
 * Tabla: drinkgo.pedido_item
 */
@Entity
@Table(name = "pedido_item", schema = "drinkgo")
public class PedidoItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;
    
    @Column(name = "producto_id")
    private Long productoId;
    
    @Column(name = "combo_id")
    private Long comboId;
    
    @Column(name = "codigo_producto", length = 50)
    private String codigoProducto;
    
    @Column(name = "nombre_producto", nullable = false, length = 200)
    private String nombreProducto;
    
    @Column(name = "cantidad", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;
    
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(name = "descuento", nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "promocion_id")
    private Long promocionId;
    
    @Column(name = "notas", length = 250)
    private String notas;
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    
    public Long getComboId() { return comboId; }
    public void setComboId(Long comboId) { this.comboId = comboId; }
    
    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }
    
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public Long getPromocionId() { return promocionId; }
    public void setPromocionId(Long promocionId) { this.promocionId = promocionId; }
    
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    
    // Método de utilidad
    public void calcularSubtotal() {
        this.subtotal = cantidad.multiply(precioUnitario).subtract(descuento);
    }
}
