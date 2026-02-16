package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Items de transferencia de inventario.
 * Mapeo de la tabla detalle_transferencias_inventario de drinkgo_database.sql.
 */
@Entity
@Table(name = "detalle_transferencias_inventario")
public class DetalleTransferenciaInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transferencia_id", nullable = false, insertable = false, updatable = false)
    private Long transferenciaId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferencia_id", nullable = false)
    private TransferenciaInventario transferencia;

    @Column(name = "producto_id", nullable = false, insertable = false, updatable = false)
    private Long productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "lote_id", insertable = false, updatable = false)
    private Long loteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private LoteInventario lote;

    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada;

    @Column(name = "cantidad_enviada")
    private Integer cantidadEnviada;

    @Column(name = "cantidad_recibida")
    private Integer cantidadRecibida;

    @Column(name = "notas", length = 300)
    private String notas;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTransferenciaId() { 
        return transferencia != null ? transferencia.getId() : transferenciaId; 
    }

    public TransferenciaInventario getTransferencia() { return transferencia; }
    public void setTransferencia(TransferenciaInventario transferencia) { 
        this.transferencia = transferencia; 
    }

    public Long getProductoId() { 
        return producto != null ? producto.getId() : productoId; 
    }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Long getLoteId() { 
        return lote != null ? lote.getId() : loteId; 
    }

    public LoteInventario getLote() { return lote; }
    public void setLote(LoteInventario lote) { this.lote = lote; }

    public Integer getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(Integer cantidadSolicitada) { 
        this.cantidadSolicitada = cantidadSolicitada; 
    }

    public Integer getCantidadEnviada() { return cantidadEnviada; }
    public void setCantidadEnviada(Integer cantidadEnviada) { 
        this.cantidadEnviada = cantidadEnviada; 
    }

    public Integer getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Integer cantidadRecibida) { 
        this.cantidadRecibida = cantidadRecibida; 
    }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}