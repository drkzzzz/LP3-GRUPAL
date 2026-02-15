package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entidad SesionUsuario - Tokens de sesión para autenticación
 * Tabla: drinkgo.sesion_usuario
 */
@Entity
@Table(name = "sesion_usuario", schema = "drinkgo")
public class SesionUsuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;
    
    @Column(name = "token", unique = true, nullable = false, length = 500)
    private String token;
    
    @Column(name = "ip_address", columnDefinition = "inet")
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "expira_en", nullable = false)
    private OffsetDateTime expiraEn;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
    
    @PrePersist
    protected void onCreate() {
        creadoEn = OffsetDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    
    public Usuario getUsuario() { return usuario; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public OffsetDateTime getExpiraEn() { return expiraEn; }
    public void setExpiraEn(OffsetDateTime expiraEn) { this.expiraEn = expiraEn; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    
    public boolean isExpirada() {
        return OffsetDateTime.now().isAfter(expiraEn);
    }
}
