package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

public class ZonaDeliveryDTO {
    
    private Long id;
    private Long sedeId;
    private String nombre;
    private String[] distritos;
    private BigDecimal costoDelivery;
    private Integer tiempoEstimadoMinutos;
    private BigDecimal pedidoMinimo;
    private String poligono;
    private Boolean activo;
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String[] getDistritos() { return distritos; }
    public void setDistritos(String[] distritos) { this.distritos = distritos; }
    
    public BigDecimal getCostoDelivery() { return costoDelivery; }
    public void setCostoDelivery(BigDecimal costoDelivery) { this.costoDelivery = costoDelivery; }
    
    public Integer getTiempoEstimadoMinutos() { return tiempoEstimadoMinutos; }
    public void setTiempoEstimadoMinutos(Integer tiempoEstimadoMinutos) { this.tiempoEstimadoMinutos = tiempoEstimadoMinutos; }
    
    public BigDecimal getPedidoMinimo() { return pedidoMinimo; }
    public void setPedidoMinimo(BigDecimal pedidoMinimo) { this.pedidoMinimo = pedidoMinimo; }
    
    public String getPoligono() { return poligono; }
    public void setPoligono(String poligono) { this.poligono = poligono; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
