package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entidad Almacen - Almacenes por sede
 * Tabla: drinkgo.almacen
 */
@Entity
@Table(name = "almacen", schema = "drinkgo",
       uniqueConstraints = @UniqueConstraint(columnNames = {"sede_id", "codigo"}))
public class Almacen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @Column(name = "sede_id", nullable = false)
    private Long sedeId;
    
    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "descripcion", length = 250)
    private String descripcion;
    
    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo = "general"; // general, frio, exhibicion, transito
    
    @Column(name = "capacidad_unidades")
    private Integer capacidadUnidades;
    
    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal = false;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
    
    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;
    
    @PrePersist
    protected void onCreate() {
        creadoEn = OffsetDateTime.now();
        actualizadoEn = OffsetDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = OffsetDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public Integer getCapacidadUnidades() { return capacidadUnidades; }
    public void setCapacidadUnidades(Integer capacidadUnidades) { this.capacidadUnidades = capacidadUnidades; }
    
    public Boolean getEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(Boolean esPrincipal) { this.esPrincipal = esPrincipal; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
}
