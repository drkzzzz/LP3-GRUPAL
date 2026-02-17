package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;

/**
 * Entidad PermisoSistema - Permisos granulares por módulo
 * RF-ADM-017
 */
@Entity
@Table(name = "permisos_sistema")
public class PermisoSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "modulo_id", nullable = false)
    private Long moduloId;

    @Column(name = "codigo", nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "tipo_accion", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoAccion tipoAccion = TipoAccion.ver;

    // ── Enums ──

    public enum TipoAccion {
        ver, crear, editar, eliminar, exportar, aprobar, configurar, completo
    }

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getModuloId() { return moduloId; }
    public void setModuloId(Long moduloId) { this.moduloId = moduloId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoAccion getTipoAccion() { return tipoAccion; }
    public void setTipoAccion(TipoAccion tipoAccion) { this.tipoAccion = tipoAccion; }
}
