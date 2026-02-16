package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad SesionUsuario - Tokens de sesión para autenticación
 * Adaptada para MySQL (XAMPP)
 */
@Entity
@Table(name = "sesiones_usuario")
public class SesionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;

    @Column(name = "hash_token", unique = true, nullable = false, length = 500)
    private String hashToken;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @Column(name = "agente_usuario", length = 500)
    private String agenteUsuario;

    @Column(name = "info_dispositivo", length = 200)
    private String infoDispositivo;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(name = "ultima_actividad_en", nullable = false)
    private LocalDateTime ultimaActividadEn;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        if (this.ultimaActividadEn == null) {
            this.ultimaActividadEn = LocalDateTime.now();
        }
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Usuario getUsuario() { return usuario; }

    public String getHashToken() { return hashToken; }
    public void setHashToken(String hashToken) { this.hashToken = hashToken; }

    public String getDireccionIp() { return direccionIp; }
    public void setDireccionIp(String direccionIp) { this.direccionIp = direccionIp; }

    public String getAgenteUsuario() { return agenteUsuario; }
    public void setAgenteUsuario(String agenteUsuario) { this.agenteUsuario = agenteUsuario; }

    public String getInfoDispositivo() { return infoDispositivo; }
    public void setInfoDispositivo(String infoDispositivo) { this.infoDispositivo = infoDispositivo; }

    public LocalDateTime getExpiraEn() { return expiraEn; }
    public void setExpiraEn(LocalDateTime expiraEn) { this.expiraEn = expiraEn; }

    public LocalDateTime getUltimaActividadEn() { return ultimaActividadEn; }
    public void setUltimaActividadEn(LocalDateTime ultimaActividadEn) { this.ultimaActividadEn = ultimaActividadEn; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public LocalDateTime getCreadoEn() { return creadoEn; }

    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(expiraEn);
    }
}