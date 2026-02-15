package DrinkGo.DrinkGo_backend.dto;

public class AlmacenDTO {
    
    private Long id;
    private Long sedeId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String tipo; // general, frio, exhibicion, transito
    private Integer capacidadUnidades;
    private Boolean esPrincipal;
    private Boolean activo;
    
    public AlmacenDTO() {}
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public Integer getCapacidadUnidades() { return capacidadUnidades; }
    public void setCapacidadUnidades(Integer capacidadUnidades) { this.capacidadUnidades = capacidadUnidades; }
    
    public Boolean getEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(Boolean esPrincipal) { this.esPrincipal = esPrincipal; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
