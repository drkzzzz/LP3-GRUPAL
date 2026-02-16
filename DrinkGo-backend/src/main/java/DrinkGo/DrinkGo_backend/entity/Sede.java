package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Sede - Sucursales de cada negocio (tenant)
 * Tabla: drinkgo.sede
 */
@Entity
@Table(name = "sede", schema = "drinkgo",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "codigo"}))
public class Sede {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;
    
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;
    
    @Column(name = "direccion", length = 250)
    private String direccion;
    
    @Column(name = "distrito", length = 80)
    private String distrito;
    
    @Column(name = "ciudad", length = 80)
    private String ciudad;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "coordenadas_lat", precision = 10, scale = 7)
    private BigDecimal coordenadasLat;
    
    @Column(name = "coordenadas_lng", precision = 10, scale = 7)
    private BigDecimal coordenadasLng;
    
    @Column(name = "has_tables", nullable = false)
    private Boolean hasTables = false;
    
    @Column(name = "has_delivery", nullable = false)
    private Boolean hasDelivery = true;
    
    @Column(name = "has_pickup", nullable = false)
    private Boolean hasPickup = true;
    
    @Column(name = "capacidad_mesas")
    private Integer capacidadMesas = 0;
    
    @Column(name = "almacen_principal_id")
    private Long almacenPrincipalId;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
    
    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;
    
    // Relaciones
    @OneToMany(mappedBy = "sedeId", fetch = FetchType.LAZY)
    private Set<Almacen> almacenes = new HashSet<>();
    
    @OneToOne(mappedBy = "sede", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SedeConfig configuracion;
    
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
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public BigDecimal getCoordenadasLat() { return coordenadasLat; }
    public void setCoordenadasLat(BigDecimal coordenadasLat) { this.coordenadasLat = coordenadasLat; }
    
    public BigDecimal getCoordenadasLng() { return coordenadasLng; }
    public void setCoordenadasLng(BigDecimal coordenadasLng) { this.coordenadasLng = coordenadasLng; }
    
    public Boolean getHasTables() { return hasTables; }
    public void setHasTables(Boolean hasTables) { this.hasTables = hasTables; }
    
    public Boolean getHasDelivery() { return hasDelivery; }
    public void setHasDelivery(Boolean hasDelivery) { this.hasDelivery = hasDelivery; }
    
    public Boolean getHasPickup() { return hasPickup; }
    public void setHasPickup(Boolean hasPickup) { this.hasPickup = hasPickup; }
    
    public Integer getCapacidadMesas() { return capacidadMesas; }
    public void setCapacidadMesas(Integer capacidadMesas) { this.capacidadMesas = capacidadMesas; }
    
    public Long getAlmacenPrincipalId() { return almacenPrincipalId; }
    public void setAlmacenPrincipalId(Long almacenPrincipalId) { this.almacenPrincipalId = almacenPrincipalId; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
    
    public Set<Almacen> getAlmacenes() { return almacenes; }
    public void setAlmacenes(Set<Almacen> almacenes) { this.almacenes = almacenes; }
    
    public SedeConfig getConfiguracion() { return configuracion; }
    public void setConfiguracion(SedeConfig configuracion) { this.configuracion = configuracion; }
}
