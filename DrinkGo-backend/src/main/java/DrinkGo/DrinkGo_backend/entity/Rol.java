package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Rol - Roles de usuario (globales y por tenant)
 * Tabla: drinkgo.rol
 */
@Entity
@Table(name = "rol", schema = "drinkgo",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "codigo"}))
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id")
    private Long tenantId; // NULL = rol global del sistema
    
    @Column(name = "codigo", nullable = false, length = 60)
    private String codigo;
    
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;
    
    @Column(name = "descripcion", length = 250)
    private String descripcion;
    
    @Column(name = "es_sistema", nullable = false)
    private Boolean esSistema = false;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
    
    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;
    
    // Relación con permisos
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rol_permiso",
        schema = "drinkgo",
        joinColumns = @JoinColumn(name = "rol_id"),
        inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();
    
    // Usuarios con este rol
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>();
    
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
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Boolean getEsSistema() { return esSistema; }
    public void setEsSistema(Boolean esSistema) { this.esSistema = esSistema; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
    
    public Set<Permiso> getPermisos() { return permisos; }
    public void setPermisos(Set<Permiso> permisos) { this.permisos = permisos; }
    
    public Set<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(Set<Usuario> usuarios) { this.usuarios = usuarios; }
    
    // Métodos de utilidad
    public void addPermiso(Permiso permiso) {
        this.permisos.add(permiso);
    }
    
    public void removePermiso(Permiso permiso) {
        this.permisos.remove(permiso);
    }
    
    public boolean esRolGlobal() {
        return tenantId == null;
    }
}
