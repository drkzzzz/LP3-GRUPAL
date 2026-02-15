package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para actualizar una devolución existente.
 */
public class UpdateDevolucionRequest {

    private String categoriaMotivo;
    private String detalleMotivo;
    private String metodoReembolso;
    private BigDecimal subtotal;
    private BigDecimal montoImpuesto;
    private BigDecimal total;
    private String notas;
    private List<DevolucionItemDTO> detalles;

    // --- Getters y Setters ---

    public String getCategoriaMotivo() {
        return categoriaMotivo;
    }

    public void setCategoriaMotivo(String categoriaMotivo) {
        this.categoriaMotivo = categoriaMotivo;
    }

    public String getDetalleMotivo() {
        return detalleMotivo;
    }

    public void setDetalleMotivo(String detalleMotivo) {
        this.detalleMotivo = detalleMotivo;
    }

    public String getMetodoReembolso() {
        return metodoReembolso;
    }

    public void setMetodoReembolso(String metodoReembolso) {
        this.metodoReembolso = metodoReembolso;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getMontoImpuesto() {
        return montoImpuesto;
    }

    public void setMontoImpuesto(BigDecimal montoImpuesto) {
        this.montoImpuesto = montoImpuesto;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<DevolucionItemDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DevolucionItemDTO> detalles) {
        this.detalles = detalles;
    }
}
