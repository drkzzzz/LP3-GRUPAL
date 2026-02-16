package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entidad UsuarioRecuperacion - Tokens para recuperación de contraseña
 * Tabla: drinkgo.usuario_recuperacion
 */
@Entity
@Table(name = "usuario_recuperacion", schema = "drinkgo")
public class UsuarioRecuperacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;
    
    @Column(name = "token", unique = true, nullable = false, length = 120)
    private String token;
    
    @Column(name = "expira_en", nullable = false)
    private OffsetDateTime expiraEn;
    
    @Column(name = "usado", nullable = false)
    private Boolean usado = false;
    
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
    
    public OffsetDateTime getExpiraEn() { return expiraEn; }
    public void setExpiraEn(OffsetDateTime expiraEn) { this.expiraEn = expiraEn; }
    
    public Boolean getUsado() { return usado; }
    public void setUsado(Boolean usado) { this.usado = usado; }
    
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    
    public boolean isExpirado() {
        return OffsetDateTime.now().isAfter(expiraEn);
    }
    
    public boolean isValido() {
        return !usado && !isExpirado();
    }
}
