package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para crear un nuevo gasto.
 * RF-FIN-013: Registrar Gastos y Egresos
 */
public class CreateGastoRequest {

    private Long negocioId;
    private Long sedeId;
    private Long categoriaId;
    private Long proveedorId;
    private String descripcion;
    private BigDecimal monto;
    private BigDecimal montoImpuesto;
    private BigDecimal total;
    private String moneda;
    private LocalDate fechaGasto;
    private String metodoPago; // efectivo, transferencia_bancaria, tarjeta_credito, cheque, otro
    private String referenciaPago;
    private String urlComprobante;
    private Boolean esRecurrente;
    private String periodoRecurrencia; // semanal, quincenal, mensual, trimestral, anual
    private Long registradoPor;
    private String notas;

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

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
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

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public LocalDate getFechaGasto() {
        return fechaGasto;
    }

    public void setFechaGasto(LocalDate fechaGasto) {
        this.fechaGasto = fechaGasto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
    }

    public String getUrlComprobante() {
        return urlComprobante;
    }

    public void setUrlComprobante(String urlComprobante) {
        this.urlComprobante = urlComprobante;
    }

    public Boolean getEsRecurrente() {
        return esRecurrente;
    }

    public void setEsRecurrente(Boolean esRecurrente) {
        this.esRecurrente = esRecurrente;
    }

    public String getPeriodoRecurrencia() {
        return periodoRecurrencia;
    }

    public void setPeriodoRecurrencia(String periodoRecurrencia) {
        this.periodoRecurrencia = periodoRecurrencia;
    }

    public Long getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(Long registradoPor) {
        this.registradoPor = registradoPor;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
