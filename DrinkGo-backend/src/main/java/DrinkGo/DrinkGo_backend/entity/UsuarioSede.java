package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad UsuarioSede - Asignación de usuarios a sedes
 * Tabla: usuarios_sedes
 * RF-ADM-015
 */
@Entity
@Table(name = "usuarios_sedes", uniqueConstraints = @UniqueConstraint(columnNames = { "usuario_id", "sede_id" }))
public class UsuarioSede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Column(name = "es_predeterminado", nullable = false)
    private Boolean esPredeterminado = false;

    @Column(name = "asignado_en", nullable = false, updatable = false)
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
    }

    public Boolean getEsPredeterminado() {
        return esPredeterminado;
    }

    public void setEsPredeterminado(Boolean esPredeterminado) {
        this.esPredeterminado = esPredeterminado;
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
}
