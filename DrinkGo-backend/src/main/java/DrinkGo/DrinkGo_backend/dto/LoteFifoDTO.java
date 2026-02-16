package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resultados de la función drinkgo.seleccionar_lotes_fifo()
 */
public class LoteFifoDTO {
    
    private Long loteId;
    private BigDecimal cantidadADescontar;
    private LocalDate fechaVencimiento;
    
    public LoteFifoDTO() {}
    
    public LoteFifoDTO(Long loteId, BigDecimal cantidadADescontar, LocalDate fechaVencimiento) {
        this.loteId = loteId;
        this.cantidadADescontar = cantidadADescontar;
        this.fechaVencimiento = fechaVencimiento;
    }
    
    // Getters y Setters
    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }
    
    public BigDecimal getCantidadADescontar() { return cantidadADescontar; }
    public void setCantidadADescontar(BigDecimal cantidadADescontar) { this.cantidadADescontar = cantidadADescontar; }
    
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
