package DrinkGo.DrinkGo_backend.dto;

import java.util.Set;

public class RolDTO {
    
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Boolean esSistema;
    private Boolean activo;
    private Set<Long> permisosIds;
    
    public RolDTO() {}
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Boolean getEsSistema() { return esSistema; }
    public void setEsSistema(Boolean esSistema) { this.esSistema = esSistema; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public Set<Long> getPermisosIds() { return permisosIds; }
    public void setPermisosIds(Set<Long> permisosIds) { this.permisosIds = permisosIds; }
}
