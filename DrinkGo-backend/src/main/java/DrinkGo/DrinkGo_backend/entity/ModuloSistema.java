package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;

/**
 * Entidad ModuloSistema - Módulos del sistema para control de acceso
 * RF-ADM-017
 */
@Entity
@Table(name = "modulos_sistema")
public class ModuloSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "modulo_padre_id")
    private Long moduloPadreId;

    @Column(name = "icono", length = 50)
    private String icono;

    @Column(name = "orden", nullable = false)
    private Integer orden = 0;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getModuloPadreId() { return moduloPadreId; }
    public void setModuloPadreId(Long moduloPadreId) { this.moduloPadreId = moduloPadreId; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }
}
