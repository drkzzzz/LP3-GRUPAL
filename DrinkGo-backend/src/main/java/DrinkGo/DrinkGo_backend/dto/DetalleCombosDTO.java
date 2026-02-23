package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DetalleCombosDTO {

    private Long id;
    private Long comboId;
    private Long productoId;
    private BigDecimal cantidad;
    private LocalDateTime creadoEn;

    // Constructores
    public DetalleCombosDTO() {
    }

    public DetalleCombosDTO(Long id, Long comboId, Long productoId, BigDecimal cantidad, LocalDateTime creadoEn) {
        this.id = id;
        this.comboId = comboId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.creadoEn = creadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getComboId() {
        return comboId;
    }

    public void setComboId(Long comboId) {
        this.comboId = comboId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
