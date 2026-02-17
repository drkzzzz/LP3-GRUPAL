package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO Request para PermisoSistema
 */
public class PermisoSistemaRequest {

    private Long moduloId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String tipoAccion; // ver, crear, editar, eliminar, exportar, aprobar, configurar, completo

    // ── Getters y Setters ──

    public Long getModuloId() { return moduloId; }
    public void setModuloId(Long moduloId) { this.moduloId = moduloId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipoAccion() { return tipoAccion; }
    public void setTipoAccion(String tipoAccion) { this.tipoAccion = tipoAccion; }
}
