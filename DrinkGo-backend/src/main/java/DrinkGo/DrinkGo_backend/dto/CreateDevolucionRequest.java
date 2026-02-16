package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para crear una nueva devolución.
 * RF-FIN-008: Registrar Solicitudes de Devolución.
 */
public class CreateDevolucionRequest {

    private Long negocioId;
    private Long sedeId;
    private Long ventaId;
    private Long pedidoId;
    private Long clienteId;
    private String tipoDevolucion; // total / parcial
    private String categoriaMotivo; // defectuoso, articulo_incorrecto, etc.
    private String detalleMotivo;
    private String metodoReembolso; // efectivo, pago_original, credito_tienda, transferencia_bancaria
    private BigDecimal subtotal;
    private BigDecimal montoImpuesto;
    private BigDecimal total;
    private Long solicitadoPor;
    private String notas;
    private List<DevolucionItemDTO> detalles;

    // --- Getters y Setters ---

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getTipoDevolucion() {
        return tipoDevolucion;
    }

    public void setTipoDevolucion(String tipoDevolucion) {
        this.tipoDevolucion = tipoDevolucion;
    }

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

    public Long getSolicitadoPor() {
        return solicitadoPor;
    }

    public void setSolicitadoPor(Long solicitadoPor) {
        this.solicitadoPor = solicitadoPor;
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
