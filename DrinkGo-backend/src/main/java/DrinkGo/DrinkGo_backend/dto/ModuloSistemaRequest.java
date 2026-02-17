package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO Request para ModuloSistema
 */
public class ModuloSistemaRequest {

    private String codigo;
    private String nombre;
    private String descripcion;
    private Long moduloPadreId;
    private String icono;
    private Integer orden;
    private Boolean estaActivo;

    // ── Getters y Setters ──

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
