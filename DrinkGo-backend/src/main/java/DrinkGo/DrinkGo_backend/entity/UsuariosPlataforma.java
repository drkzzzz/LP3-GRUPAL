package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_plataforma")
@SQLDelete(sql = "UPDATE usuarios_plataforma SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "uuid", "email", "hashContrasena", "nombres", "apellidos", "telefono", "rol",
        "estaActivo", "ultimoAccesoEn", "contrasenacambiadaEn", "intentosFallidosAcceso", "bloqueadoHasta",
        "creadoEn", "actualizadoEn" })
public class UsuariosPlataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 36)
    private String uuid;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "hash_contrasena", nullable = false)
    private String hashContrasena;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    private String telefono;

    @Enumerated(EnumType.STRING)
    private RolPlataforma rol = RolPlataforma.superadmin;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "ultimo_acceso_en")
    private LocalDateTime ultimoAccesoEn;

    @Column(name = "contrasena_cambiada_en")
    private LocalDateTime contrasenaCambiadaEn;

    @Column(name = "intentos_fallidos_acceso")
    private Integer intentosFallidosAcceso = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    public enum RolPlataforma {
        superadmin, soporte_plataforma, visualizador_plataforma
    }

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
        if (uuid == null) {
            uuid = java.util.UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashContrasena() {
        return hashContrasena;
    }

    public void setHashContrasena(String hashContrasena) {
        this.hashContrasena = hashContrasena;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public RolPlataforma getRol() {
        return rol;
    }

    public void setRol(RolPlataforma rol) {
        this.rol = rol;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public LocalDateTime getUltimoAccesoEn() {
        return ultimoAccesoEn;
    }

    public void setUltimoAccesoEn(LocalDateTime ultimoAccesoEn) {
        this.ultimoAccesoEn = ultimoAccesoEn;
    }

    public LocalDateTime getContrasenaCambiadaEn() {
        return contrasenaCambiadaEn;
    }

    public void setContrasenaCambiadaEn(LocalDateTime contrasenaCambiadaEn) {
        this.contrasenaCambiadaEn = contrasenaCambiadaEn;
    }

    public Integer getIntentosFallidosAcceso() {
        return intentosFallidosAcceso;
    }

    public void setIntentosFallidosAcceso(Integer intentosFallidosAcceso) {
        this.intentosFallidosAcceso = intentosFallidosAcceso;
    }

    public LocalDateTime getBloqueadoHasta() {
        return bloqueadoHasta;
    }

    public void setBloqueadoHasta(LocalDateTime bloqueadoHasta) {
        this.bloqueadoHasta = bloqueadoHasta;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    @Override
    public String toString() {
        return "UsuariosPlataforma [id=" + id + ", uuid=" + uuid + ", email=" + email + ", nombres=" + nombres
                + ", apellidos=" + apellidos + ", telefono=" + telefono + ", rol=" + rol + ", estaActivo=" + estaActivo
                + ", ultimoAccesoEn=" + ultimoAccesoEn + ", intentosFallidosAcceso=" + intentosFallidosAcceso
                + ", bloqueadoHasta=" + bloqueadoHasta + ", creadoEn=" + creadoEn + ", actualizadoEn=" + actualizadoEn
                + "]";
    }
}
