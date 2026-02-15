package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad para la tabla sesiones_usuario.
 * Almacena las sesiones activas con hash SHA-256 del token JWT.
 * El token plano NUNCA se almacena en base de datos.
 */
@Entity
@Table(name = "sesiones_usuario")
public class SesionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_plataforma_id")
    private Long usuarioPlataformaId;

    @Column(name = "hash_token", nullable = false)
    private String hashToken;

    @Column(name = "direccion_ip")
    private String direccionIp;

    @Column(name = "agente_usuario")
    private String agenteUsuario;

    @Column(name = "info_dispositivo")
    private String infoDispositivo;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(name = "ultima_actividad_en", nullable = false)
    private LocalDateTime ultimaActividadEn;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getUsuarioPlataformaId() { return usuarioPlataformaId; }
    public void setUsuarioPlataformaId(Long usuarioPlataformaId) { this.usuarioPlataformaId = usuarioPlataformaId; }

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
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
