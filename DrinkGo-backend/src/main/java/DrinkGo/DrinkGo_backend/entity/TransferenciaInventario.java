package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Transferencias entre almacenes (RF-INV-005).
 * Mapeo de la tabla transferencias_inventario de drinkgo_database.sql.
 * Mantiene trazabilidad completa del proceso: solicitud -> tránsito -> recepción.
 */
@Entity
@Table(name = "transferencias_inventario")
@SQLDelete(sql = "UPDATE transferencias_inventario SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class TransferenciaInventario {

    public enum TransferenciaEstado {
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
    private TransferenciaEstado estado = TransferenciaEstado.borrador;

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

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    @OneToMany(mappedBy = "transferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleTransferenciaInventario> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
        if (this.solicitadoEn == null) this.solicitadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getNumeroTransferencia() { return numeroTransferencia; }
    public void setNumeroTransferencia(String numeroTransferencia) { this.numeroTransferencia = numeroTransferencia; }

    public Long getAlmacenOrigenId() { 
        return almacenOrigen != null ? almacenOrigen.getId() : almacenOrigenId; 
    }

    public Almacen getAlmacenOrigen() { return almacenOrigen; }
    public void setAlmacenOrigen(Almacen almacenOrigen) { this.almacenOrigen = almacenOrigen; }

    public Long getAlmacenDestinoId() { 
        return almacenDestino != null ? almacenDestino.getId() : almacenDestinoId; 
    }

    public Almacen getAlmacenDestino() { return almacenDestino; }
    public void setAlmacenDestino(Almacen almacenDestino) { this.almacenDestino = almacenDestino; }

    public TransferenciaEstado getEstado() { return estado; }
    public void setEstado(TransferenciaEstado estado) { this.estado = estado; }

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

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }

    public LocalDateTime getEliminadoEn() { return eliminadoEn; }
    public void setEliminadoEn(LocalDateTime eliminadoEn) { this.eliminadoEn = eliminadoEn; }

    public List<DetalleTransferenciaInventario> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleTransferenciaInventario> detalles) { this.detalles = detalles; }
}