package DrinkGo.DrinkGo_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad OrdenCompra - Mapea la tabla 'ordenes_compra' de drinkgo_database.sql.
 * Bloque 6: Proveedores y Compras (RF-COM-004..007).
 */
@Entity
@Table(name = "ordenes_compra")
public class OrdenCompra {

    public enum EstadoOrden {
        pendiente, recibida, cancelada
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "numero_orden", nullable = false, length = 30)
    private String numeroOrden;

    @Column(name = "proveedor_id", nullable = false)
    private Long proveedorId;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Column(name = "almacen_id", nullable = false)
    private Long almacenId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoOrden estado = EstadoOrden.pendiente;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "creado_por")
    private Long creadoPor;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }

    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }

    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    public Long getAlmacenId() { return almacenId; }
    public void setAlmacenId(Long almacenId) { this.almacenId = almacenId; }

    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}
