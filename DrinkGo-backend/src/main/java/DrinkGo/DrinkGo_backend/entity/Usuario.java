package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad Usuario - Usuarios/empleados de cada licorería
 * Tabla: drinkgo.usuario
 */
@Entity
@Table(name = "usuario", schema = "drinkgo",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "email"}))
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @Column(name = "uuid", unique = true, nullable = false)
    private UUID uuid;
    
    @Column(name = "codigo_empleado", length = 30)
    private String codigoEmpleado;
    
    @Column(name = "nombres", nullable = false, length = 120)
    private String nombres;
    
    @Column(name = "apellidos", length = 120)
    private String apellidos;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "contrasena_hash", nullable = false, length = 200)
    private String contrasenaHash;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Column(name = "sede_preferida_id")
    private Long sedePreferidaId;
    
    @Column(name = "pin_rapido", length = 10)
    private String pinRapido;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "ultimo_acceso")
    private OffsetDateTime ultimoAcceso;
    
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
    
    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;
    
    // Relaciones
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_rol",
        schema = "drinkgo",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_sede",
        schema = "drinkgo",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "sede_id")
    )
    private Set<Sede> sedes = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
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
    
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    
    public String getCodigoEmpleado() { return codigoEmpleado; }
    public void setCodigoEmpleado(String codigoEmpleado) { this.codigoEmpleado = codigoEmpleado; }
    
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getContrasenaHash() { return contrasenaHash; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }
    
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    
    public Long getSedePreferidaId() { return sedePreferidaId; }
    public void setSedePreferidaId(Long sedePreferidaId) { this.sedePreferidaId = sedePreferidaId; }
    
    public String getPinRapido() { return pinRapido; }
    public void setPinRapido(String pinRapido) { this.pinRapido = pinRapido; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public OffsetDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(OffsetDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
    
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
    
    public Set<Rol> getRoles() { return roles; }
    public void setRoles(Set<Rol> roles) { this.roles = roles; }
    
    public Set<Sede> getSedes() { return sedes; }
    public void setSedes(Set<Sede> sedes) { this.sedes = sedes; }
    
    // Métodos de utilidad
    public void addRol(Rol rol) {
        this.roles.add(rol);
    }
    
    public void removeRol(Rol rol) {
        this.roles.remove(rol);
    }
    
    public void addSede(Sede sede) {
        this.sedes.add(sede);
    }
    
    public String getNombreCompleto() {
        return nombres + (apellidos != null ? " " + apellidos : "");
    }
}
