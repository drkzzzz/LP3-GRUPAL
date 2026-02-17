package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;

/**
 * Entidad DetalleRecepcionCompra - Detalles de recepciones de compra
 * Tabla: detalle_recepciones_compra
 */
@Entity
@Table(name = "detalle_recepciones_compra")
public class DetalleRecepcionCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recepcion_id", nullable = false)
    private Long recepcionId;

    @Column(name = "detalle_orden_compra_id", nullable = false)
    private Long detalleOrdenCompraId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "lote_id")
    private Long loteId;

    @Column(name = "cantidad_recibida", nullable = false)
    private Integer cantidadRecibida;

    @Column(name = "cantidad_rechazada")
    private Integer cantidadRechazada = 0;

    @Column(name = "razon_rechazo", length = 300)
    private String razonRechazo;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRecepcionId() { return recepcionId; }
    public void setRecepcionId(Long recepcionId) { this.recepcionId = recepcionId; }

    public Long getDetalleOrdenCompraId() { return detalleOrdenCompraId; }
    public void setDetalleOrdenCompraId(Long detalleOrdenCompraId) { this.detalleOrdenCompraId = detalleOrdenCompraId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }

    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Integer cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    public Integer getCantidadRechazada() { return cantidadRechazada; }
    public void setCantidadRechazada(Integer cantidadRechazada) { this.cantidadRechazada = cantidadRechazada; }

    public String getRazonRechazo() { return razonRechazo; }
    public void setRazonRechazo(String razonRechazo) { this.razonRechazo = razonRechazo; }
}
