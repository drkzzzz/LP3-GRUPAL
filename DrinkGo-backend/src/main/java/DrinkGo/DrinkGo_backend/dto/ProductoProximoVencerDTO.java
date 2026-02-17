package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resultados de la función drinkgo.obtener_productos_proximos_vencer()
 */
public class ProductoProximoVencerDTO {
    
    private Long loteId;
    private Long productoId;
    private String productoNombre;
    private Long sedeId;
    private String sedeNombre;
    private BigDecimal cantidadDisponible;
    private LocalDate fechaVencimiento;
    private Integer diasRestantes;
    private String nivelUrgencia;
    
    public ProductoProximoVencerDTO() {}
    
    public ProductoProximoVencerDTO(Long loteId, Long productoId, String productoNombre,
                                     Long sedeId, String sedeNombre, BigDecimal cantidadDisponible,
                                     LocalDate fechaVencimiento, Integer diasRestantes, String nivelUrgencia) {
        this.loteId = loteId;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.sedeId = sedeId;
        this.sedeNombre = sedeNombre;
        this.cantidadDisponible = cantidadDisponible;
        this.fechaVencimiento = fechaVencimiento;
        this.diasRestantes = diasRestantes;
        this.nivelUrgencia = nivelUrgencia;
    }
    
    // Getters y Setters
    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }
    
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
    
    public String getSedeNombre() { return sedeNombre; }
    public void setSedeNombre(String sedeNombre) { this.sedeNombre = sedeNombre; }
    
    public BigDecimal getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(BigDecimal cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
    
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    
    public Integer getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(Integer diasRestantes) { this.diasRestantes = diasRestantes; }
    
    public String getNivelUrgencia() { return nivelUrgencia; }
    public void setNivelUrgencia(String nivelUrgencia) { this.nivelUrgencia = nivelUrgencia; }
}
