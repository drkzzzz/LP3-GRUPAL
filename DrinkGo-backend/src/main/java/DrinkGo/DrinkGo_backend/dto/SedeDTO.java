package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

public class SedeDTO {
    
    private Long id;
    private String codigo;
    private String nombre;
    private String direccion;
    private String distrito;
    private String ciudad;
    private String telefono;
    private String email;
    private BigDecimal coordenadasLat;
    private BigDecimal coordenadasLng;
    private Boolean hasTables;
    private Boolean hasDelivery;
    private Boolean hasPickup;
    private Integer capacidadMesas;
    private Boolean activo;
    
    public SedeDTO() {}
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public BigDecimal getCoordenadasLat() { return coordenadasLat; }
    public void setCoordenadasLat(BigDecimal coordenadasLat) { this.coordenadasLat = coordenadasLat; }
    
    public BigDecimal getCoordenadasLng() { return coordenadasLng; }
    public void setCoordenadasLng(BigDecimal coordenadasLng) { this.coordenadasLng = coordenadasLng; }
    
    public Boolean getHasTables() { return hasTables; }
    public void setHasTables(Boolean hasTables) { this.hasTables = hasTables; }
    
    public Boolean getHasDelivery() { return hasDelivery; }
    public void setHasDelivery(Boolean hasDelivery) { this.hasDelivery = hasDelivery; }
    
    public Boolean getHasPickup() { return hasPickup; }
    public void setHasPickup(Boolean hasPickup) { this.hasPickup = hasPickup; }
    
    public Integer getCapacidadMesas() { return capacidadMesas; }
    public void setCapacidadMesas(Integer capacidadMesas) { this.capacidadMesas = capacidadMesas; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
