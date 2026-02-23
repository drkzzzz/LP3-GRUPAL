package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_roles")
@JsonPropertyOrder({ "id", "usuarioId", "rolId", "asignadoEn", "asignadoPor" })
public class UsuariosRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Roles rol;

    @Column(name = "asignado_en")
    private LocalDateTime asignadoEn;

    @Column(name = "asignado_por")
    private Long asignadoPor;

    @PrePersist
    protected void onCreate() {
        asignadoEn = LocalDateTime.now();
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

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }

    public LocalDateTime getAsignadoEn() {
        return asignadoEn;
    }

    public void setAsignadoEn(LocalDateTime asignadoEn) {
        this.asignadoEn = asignadoEn;
    }

    public Long getAsignadoPor() {
        return asignadoPor;
    }

    public void setAsignadoPor(Long asignadoPor) {
        this.asignadoPor = asignadoPor;
    }

    @Override
    public String toString() {
        return "UsuariosRoles [id=" + id + ", usuario=" + (usuario != null ? usuario.getId() : null) + ", rol="
                + (rol != null ? rol.getId() : null) + ", asignadoEn=" + asignadoEn + ", asignadoPor=" + asignadoPor
                + "]";
    }
}
