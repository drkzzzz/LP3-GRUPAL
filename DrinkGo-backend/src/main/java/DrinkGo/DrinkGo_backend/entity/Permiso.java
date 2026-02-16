package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Permiso - Permisos del sistema
 * Tabla: drinkgo.permiso
 */
@Entity
@Table(name = "permiso", schema = "drinkgo")
public class Permiso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codigo", unique = true, nullable = false, length = 80)
    private String codigo;
    
    @Column(name = "nombre", length = 80)
    private String nombre;
    
    @Column(name = "descripcion", length = 250)
    private String descripcion;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
    
    // Roles que tienen este permiso
    @ManyToMany(mappedBy = "permisos", fetch = FetchType.LAZY)
    private Set<Rol> roles = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        creadoEn = OffsetDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    
    public Set<Rol> getRoles() { return roles; }
    public void setRoles(Set<Rol> roles) { this.roles = roles; }
}
