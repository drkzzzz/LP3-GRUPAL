package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de referencia para la relación usuario-rol.
 * Utilizada para obtener los roles asignados al usuario autenticado.
 */
@Entity
@Table(name = "usuarios_roles")
public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "rol_id", nullable = false, insertable = false, updatable = false)
    private Long rolId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(name = "asignado_en", nullable = false, updatable = false)
    private LocalDateTime asignadoEn;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getRolId() { return rolId; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public LocalDateTime getAsignadoEn() { return asignadoEn; }
    public void setAsignadoEn(LocalDateTime asignadoEn) { this.asignadoEn = asignadoEn; }
}
