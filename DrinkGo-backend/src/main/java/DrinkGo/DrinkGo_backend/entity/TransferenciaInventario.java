package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Transferencias entre almacenes (RF-INV-005).
 * Mapeo exacto de la tabla transferencias_inventario de drinkgo_database.sql.
 * Mantiene trazabilidad completa del proceso de transferencia.
 */
@Entity
@Table(name = "transferencias_inventario")
public class TransferenciaInventario {

    public enum EstadoTransferencia {
        borrador, pendiente, en_transito, recibida, cancelada
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "numero_transferencia", nullable = false, length = 30)
    private String numeroTransferencia;

    @Column(name = "almacen_origen_id", nullable = false, insertable = false, updatable = false)
    private Long almacenOrigenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_origen_id", nullable = false)
    private Almacen almacenOrigen;

    @Column(name = "almacen_destino_id", nullable = false, insertable = false, updatable = false)
    private Long almacenDestinoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_destino_id", nullable = false)
    private Almacen almacenDestino;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoTransferencia estado = EstadoTransferencia.borrador;

    @Column(name = "solicitado_por")
    private Long solicitadoPor;

    @Column(name = "aprobado_por")
    private Long aprobadoPor;

    @Column(name = "recibido_por")
    private Long recibidoPor;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "solicitado_en")
    private LocalDateTime solicitadoEn;

    @Column(name = "aprobado_en")
    private LocalDateTime aprobadoEn;

    @Column(name = "despachado_en")
    private LocalDateTime despachadoEn;

    @Column(name = "recibido_en")
    private LocalDateTime recibidoEn;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false, insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    @OneToMany(mappedBy = "transferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleTransferenciaInventario> detalles = new ArrayList<>();

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getNumeroTransferencia() { return numeroTransferencia; }
    public void setNumeroTransferencia(String numeroTransferencia) { this.numeroTransferencia = numeroTransferencia; }

    public Long getAlmacenOrigenId() { return almacenOrigen != null ? almacenOrigen.getId() : almacenOrigenId; }

    public Almacen getAlmacenOrigen() { return almacenOrigen; }
    public void setAlmacenOrigen(Almacen almacenOrigen) { this.almacenOrigen = almacenOrigen; }

    public Long getAlmacenDestinoId() { return almacenDestino != null ? almacenDestino.getId() : almacenDestinoId; }

    public Almacen getAlmacenDestino() { return almacenDestino; }
    public void setAlmacenDestino(Almacen almacenDestino) { this.almacenDestino = almacenDestino; }

    public EstadoTransferencia getEstado() { return estado; }
    public void setEstado(EstadoTransferencia estado) { this.estado = estado; }

    public Long getSolicitadoPor() { return solicitadoPor; }
    public void setSolicitadoPor(Long solicitadoPor) { this.solicitadoPor = solicitadoPor; }

    public Long getAprobadoPor() { return aprobadoPor; }
    public void setAprobadoPor(Long aprobadoPor) { this.aprobadoPor = aprobadoPor; }

    public Long getRecibidoPor() { return recibidoPor; }
    public void setRecibidoPor(Long recibidoPor) { this.recibidoPor = recibidoPor; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public LocalDateTime getSolicitadoEn() { return solicitadoEn; }
    public void setSolicitadoEn(LocalDateTime solicitadoEn) { this.solicitadoEn = solicitadoEn; }

    public LocalDateTime getAprobadoEn() { return aprobadoEn; }
    public void setAprobadoEn(LocalDateTime aprobadoEn) { this.aprobadoEn = aprobadoEn; }

    public LocalDateTime getDespachadoEn() { return despachadoEn; }
    public void setDespachadoEn(LocalDateTime despachadoEn) { this.despachadoEn = despachadoEn; }

    public LocalDateTime getRecibidoEn() { return recibidoEn; }
    public void setRecibidoEn(LocalDateTime recibidoEn) { this.recibidoEn = recibidoEn; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }

    public List<DetalleTransferenciaInventario> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleTransferenciaInventario> detalles) { this.detalles = detalles; }
}
