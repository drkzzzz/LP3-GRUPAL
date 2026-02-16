package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Almacen - Gestión de almacenes por sede.
 * Alineado con drinkgo_database.sql: tabla almacenes.
 * Unique key: (negocio_id, codigo). ENUM tipo_almacenamiento: ambiente, frio, congelado, mixto.
 */
@Entity
@Table(name = "almacenes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"negocio_id", "codigo"}))
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "tipo_almacenamiento", nullable = false, length = 30)
    private String tipoAlmacenamiento = "ambiente";

    @Column(name = "temperatura_min", precision = 5, scale = 2)
    private BigDecimal temperaturaMin;

    @Column(name = "temperatura_max", precision = 5, scale = 2)
    private BigDecimal temperaturaMax;

    @Column(name = "descripcion_capacidad", length = 300)
    private String descripcionCapacidad;

    @Column(name = "es_predeterminado", nullable = false)
    private Boolean esPredeterminado = false;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
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

    /** @deprecated Use negocioId for tenant isolation. sedeId kept for DB compatibility. */
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    // Backward compat alias for master-branch services expecting tenantId
    public Long getTenantId() { return negocioId; }
    public void setTenantId(Long tenantId) { this.negocioId = tenantId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcionCapacidad() { return descripcionCapacidad; }
    public void setDescripcionCapacidad(String descripcionCapacidad) { this.descripcionCapacidad = descripcionCapacidad; }

    // Backward compat alias for services calling getDescripcion/setDescripcion
    public String getDescripcion() { return descripcionCapacidad; }
    public void setDescripcion(String descripcion) { this.descripcionCapacidad = descripcion; }

    public String getTipoAlmacenamiento() { return tipoAlmacenamiento; }
    public void setTipoAlmacenamiento(String tipoAlmacenamiento) { this.tipoAlmacenamiento = tipoAlmacenamiento; }

    // Backward compat alias for AlmacenService.setTipo()
    public String getTipo() { return tipoAlmacenamiento; }
    public void setTipo(String tipo) { this.tipoAlmacenamiento = tipo; }

    public BigDecimal getTemperaturaMin() { return temperaturaMin; }
    public void setTemperaturaMin(BigDecimal temperaturaMin) { this.temperaturaMin = temperaturaMin; }

    public BigDecimal getTemperaturaMax() { return temperaturaMax; }
    public void setTemperaturaMax(BigDecimal temperaturaMax) { this.temperaturaMax = temperaturaMax; }

    // Backward compat: getCapacidadUnidades returns null (column removed from DB)
    public Integer getCapacidadUnidades() { return null; }
    public void setCapacidadUnidades(Integer capacidadUnidades) { /* no-op, column doesn't exist */ }

    public Boolean getEsPredeterminado() { return esPredeterminado; }
    public void setEsPredeterminado(Boolean esPredeterminado) { this.esPredeterminado = esPredeterminado; }

    // Backward compat alias for master-branch services expecting esPrincipal
    public Boolean getEsPrincipal() { return esPredeterminado; }
    public void setEsPrincipal(Boolean esPrincipal) { this.esPredeterminado = esPrincipal; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    // Backward compat alias for AlmacenService.setActivo()
    public Boolean getActivo() { return estaActivo; }
    public void setActivo(Boolean activo) { this.estaActivo = activo; }

    public LocalDateTime getCreadoEn() { return creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}