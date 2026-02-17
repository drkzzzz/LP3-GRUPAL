package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad RecepcionCompra - Recepciones de mercadería
 * Tabla: recepciones_compra
 */
@Entity
@Table(name = "recepciones_compra")
public class RecepcionCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "orden_compra_id", nullable = false)
    private Long ordenCompraId;

    @Column(name = "numero_recepcion", nullable = false, length = 30)
    private String numeroRecepcion;

    @Column(name = "recibido_por")
    private Long recibidoPor;

    @Column(name = "fecha_recepcion", nullable = false)
    private LocalDate fechaRecepcion;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoRecepcion estado = EstadoRecepcion.PENDIENTE;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
    }

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getOrdenCompraId() { return ordenCompraId; }
    public void setOrdenCompraId(Long ordenCompraId) { this.ordenCompraId = ordenCompraId; }

    public String getNumeroRecepcion() { return numeroRecepcion; }
    public void setNumeroRecepcion(String numeroRecepcion) { this.numeroRecepcion = numeroRecepcion; }

    public Long getRecibidoPor() { return recibidoPor; }
    public void setRecibidoPor(Long recibidoPor) { this.recibidoPor = recibidoPor; }

    public LocalDate getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDate fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public EstadoRecepcion getEstado() { return estado; }
    public void setEstado(EstadoRecepcion estado) { this.estado = estado; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
