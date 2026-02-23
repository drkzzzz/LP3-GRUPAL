package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sesiones_usuario")
@SQLDelete(sql = "UPDATE sesiones_usuario SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "usuarioId", "hashToken", "direccionIp", "agenteUsuario", "infoDispositivo",
        "expiraEn", "ultimaActividadEn", "estaActivo", "creadoEn" })
public class SesionesUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @Column(name = "hash_token", unique = true, nullable = false, length = 500)
    private String hashToken;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @Column(name = "agente_usuario", length = 500)
    private String agenteUsuario;

    @Column(name = "info_dispositivo")
    private String infoDispositivo;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(name = "ultima_actividad_en")
    private LocalDateTime ultimaActividadEn;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        ultimaActividadEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ultimaActividadEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public String getHashToken() {
        return hashToken;
    }

    public void setHashToken(String hashToken) {
        this.hashToken = hashToken;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getAgenteUsuario() {
        return agenteUsuario;
    }

    public void setAgenteUsuario(String agenteUsuario) {
        this.agenteUsuario = agenteUsuario;
    }

    public String getInfoDispositivo() {
        return infoDispositivo;
    }

    public void setInfoDispositivo(String infoDispositivo) {
        this.infoDispositivo = infoDispositivo;
    }

    public LocalDateTime getExpiraEn() {
        return expiraEn;
    }

    public void setExpiraEn(LocalDateTime expiraEn) {
        this.expiraEn = expiraEn;
    }

    public LocalDateTime getUltimaActividadEn() {
        return ultimaActividadEn;
    }

    public void setUltimaActividadEn(LocalDateTime ultimaActividadEn) {
        this.ultimaActividadEn = ultimaActividadEn;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    @Override
    public String toString() {
        return "SesionesUsuario [id=" + id + ", usuario=" + (usuario != null ? usuario.getId() : null) + ", hashToken="
                + hashToken + ", direccionIp=" + direccionIp + ", agenteUsuario=" + agenteUsuario + ", infoDispositivo="
                + infoDispositivo + ", expiraEn=" + expiraEn + ", ultimaActividadEn=" + ultimaActividadEn
                + ", estaActivo=" + estaActivo + ", creadoEn=" + creadoEn + "]";
    }
}
