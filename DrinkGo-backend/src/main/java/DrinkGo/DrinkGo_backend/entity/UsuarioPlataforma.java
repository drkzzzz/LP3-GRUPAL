package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad UsuarioPlataforma - Usuarios SuperAdmin y soporte de la plataforma
 * RF-PLT (Usuarios administradores de la plataforma SaaS)
 */
@Entity
@Table(name = "usuarios_plataforma")
public class UsuarioPlataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, length = 36)
    private String uuid;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "hash_contrasena", nullable = false)
    private String hashContrasena;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "url_avatar", length = 500)
    private String urlAvatar;

    @Column(name = "rol", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private RolPlataforma rol = RolPlataforma.superadmin;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "ultimo_acceso_en")
    private LocalDateTime ultimoAccesoEn;

    @Column(name = "ip_ultimo_acceso", length = 45)
    private String ipUltimoAcceso;

    @Column(name = "contrasena_cambiada_en")
    private LocalDateTime contrasenaCambiadaEn;

    @Column(name = "intentos_fallidos_acceso", nullable = false)
    private Integer intentosFallidosAcceso = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    // ── Enums ──

    public enum RolPlataforma {
        superadmin, soporte_plataforma, visualizador_plataforma
    }

    // ── Lifecycle Callbacks ──

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHashContrasena() { return hashContrasena; }
    public void setHashContrasena(String hashContrasena) { this.hashContrasena = hashContrasena; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getUrlAvatar() { return urlAvatar; }
    public void setUrlAvatar(String urlAvatar) { this.urlAvatar = urlAvatar; }

    public RolPlataforma getRol() { return rol; }
    public void setRol(RolPlataforma rol) { this.rol = rol; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public LocalDateTime getUltimoAccesoEn() { return ultimoAccesoEn; }
    public void setUltimoAccesoEn(LocalDateTime ultimoAccesoEn) { this.ultimoAccesoEn = ultimoAccesoEn; }

    public String getIpUltimoAcceso() { return ipUltimoAcceso; }
    public void setIpUltimoAcceso(String ipUltimoAcceso) { this.ipUltimoAcceso = ipUltimoAcceso; }

    public LocalDateTime getContrasenaCambiadaEn() { return contrasenaCambiadaEn; }
    public void setContrasenaCambiadaEn(LocalDateTime contrasenaCambiadaEn) { this.contrasenaCambiadaEn = contrasenaCambiadaEn; }

    public Integer getIntentosFallidosAcceso() { return intentosFallidosAcceso; }
    public void setIntentosFallidosAcceso(Integer intentosFallidosAcceso) { this.intentosFallidosAcceso = intentosFallidosAcceso; }

    public LocalDateTime getBloqueadoHasta() { return bloqueadoHasta; }
    public void setBloqueadoHasta(LocalDateTime bloqueadoHasta) { this.bloqueadoHasta = bloqueadoHasta; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
