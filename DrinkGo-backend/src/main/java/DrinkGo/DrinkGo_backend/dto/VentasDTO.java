package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentasDTO {

    private Long id;
    private Long negocioId;
    private Long sedeId;
    private String numeroVenta;
    private String tipoVenta;
    private Long clienteId;
    private Long mesaId;
    private LocalDateTime fechaVenta;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal igv;
    private BigDecimal total;
    private String estadoVenta;
    private String observaciones;
    private Long usuarioId;
    private Long sesionCajaId;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private LocalDateTime eliminadoEn;

    // Constructores
    public VentasDTO() {
    }

    public VentasDTO(Long id, Long negocioId, Long sedeId, String numeroVenta, String tipoVenta, Long clienteId,
            Long mesaId, LocalDateTime fechaVenta, BigDecimal subtotal, BigDecimal descuento, BigDecimal igv,
            BigDecimal total, String estadoVenta, String observaciones, Long usuarioId, Long sesionCajaId,
            Boolean estaActivo, LocalDateTime creadoEn, LocalDateTime actualizadoEn, LocalDateTime eliminadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.sedeId = sedeId;
        this.numeroVenta = numeroVenta;
        this.tipoVenta = tipoVenta;
        this.clienteId = clienteId;
        this.mesaId = mesaId;
        this.fechaVenta = fechaVenta;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.igv = igv;
        this.total = total;
        this.estadoVenta = estadoVenta;
        this.observaciones = observaciones;
        this.usuarioId = usuarioId;
        this.sesionCajaId = sesionCajaId;
        this.estaActivo = estaActivo;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
        this.eliminadoEn = eliminadoEn;
    }

    // Getters y Setters
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

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
    }

    public String getNumeroVenta() {
        return numeroVenta;
    }

    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = numeroVenta;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstadoVenta() {
        return estadoVenta;
    }

    public void setEstadoVenta(String estadoVenta) {
        this.estadoVenta = estadoVenta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getSesionCajaId() {
        return sesionCajaId;
    }

    public void setSesionCajaId(Long sesionCajaId) {
        this.sesionCajaId = sesionCajaId;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
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

    public LocalDateTime getEliminadoEn() {
        return eliminadoEn;
    }

    public void setEliminadoEn(LocalDateTime eliminadoEn) {
        this.eliminadoEn = eliminadoEn;
    }
}
