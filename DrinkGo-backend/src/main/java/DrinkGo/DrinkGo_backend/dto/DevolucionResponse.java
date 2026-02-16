package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuesta de Devolución con todos sus detalles.
 */
public class DevolucionResponse {

    private Long id;
    private Long negocioId;
    private Long sedeId;
    private Long ventaId;
    private Long pedidoId;
    private Long clienteId;
    private String numeroDevolucion;
    private String estado;
    private String tipoDevolucion;
    private String categoriaMotivo;
    private String detalleMotivo;
    private String metodoReembolso;
    private BigDecimal subtotal;
    private BigDecimal montoImpuesto;
    private BigDecimal total;
    private String notas;
    private LocalDateTime solicitadoEn;
    private LocalDateTime aprobadoEn;
    private LocalDateTime completadoEn;
    private LocalDateTime rechazadoEn;
    private Long solicitadoPor;
    private Long procesadoPor;
    private Long aprobadoPor;
    private String razonRechazo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private List<DevolucionItemDTO> detalles;

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
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

    public String getNumeroDevolucion() {
        return numeroDevolucion;
    }

    public void setNumeroDevolucion(String numeroDevolucion) {
        this.numeroDevolucion = numeroDevolucion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
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

    public LocalDateTime getSolicitadoEn() {
        return solicitadoEn;
    }

    public void setSolicitadoEn(LocalDateTime solicitadoEn) {
        this.solicitadoEn = solicitadoEn;
    }

    public LocalDateTime getAprobadoEn() {
        return aprobadoEn;
    }

    public void setAprobadoEn(LocalDateTime aprobadoEn) {
        this.aprobadoEn = aprobadoEn;
    }

    public LocalDateTime getCompletadoEn() {
        return completadoEn;
    }

    public void setCompletadoEn(LocalDateTime completadoEn) {
        this.completadoEn = completadoEn;
    }

    public LocalDateTime getRechazadoEn() {
        return rechazadoEn;
    }

    public void setRechazadoEn(LocalDateTime rechazadoEn) {
        this.rechazadoEn = rechazadoEn;
    }

    public Long getSolicitadoPor() {
        return solicitadoPor;
    }

    public void setSolicitadoPor(Long solicitadoPor) {
        this.solicitadoPor = solicitadoPor;
    }

    public Long getProcesadoPor() {
        return procesadoPor;
    }

    public void setProcesadoPor(Long procesadoPor) {
        this.procesadoPor = procesadoPor;
    }

    public Long getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(Long aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }

    public String getRazonRechazo() {
        return razonRechazo;
    }

    public void setRazonRechazo(String razonRechazo) {
        this.razonRechazo = razonRechazo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public List<DevolucionItemDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DevolucionItemDTO> detalles) {
        this.detalles = detalles;
    }
}
