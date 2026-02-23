package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DevolucionesDTO {

    private Long id;
    private Long negocioId;
    private Long sedeId;
    private String numeroDevolucion;
    private String tipoDevolucion;
    private Long ventaId;
    private Long pedidoId;
    private LocalDateTime fechaDevolucion;
    private BigDecimal totalDevuelto;
    private String motivoGeneral;
    private String estadoDevolucion;
    private Long usuarioId;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private LocalDateTime eliminadoEn;

    // Constructores
    public DevolucionesDTO() {
    }

    public DevolucionesDTO(Long id, Long negocioId, Long sedeId, String numeroDevolucion, String tipoDevolucion,
            Long ventaId, Long pedidoId, LocalDateTime fechaDevolucion, BigDecimal totalDevuelto,
            String motivoGeneral, String estadoDevolucion, Long usuarioId, Boolean estaActivo,
            LocalDateTime creadoEn, LocalDateTime actualizadoEn, LocalDateTime eliminadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.sedeId = sedeId;
        this.numeroDevolucion = numeroDevolucion;
        this.tipoDevolucion = tipoDevolucion;
        this.ventaId = ventaId;
        this.pedidoId = pedidoId;
        this.fechaDevolucion = fechaDevolucion;
        this.totalDevuelto = totalDevuelto;
        this.motivoGeneral = motivoGeneral;
        this.estadoDevolucion = estadoDevolucion;
        this.usuarioId = usuarioId;
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

    public String getNumeroDevolucion() {
        return numeroDevolucion;
    }

    public void setNumeroDevolucion(String numeroDevolucion) {
        this.numeroDevolucion = numeroDevolucion;
    }

    public String getTipoDevolucion() {
        return tipoDevolucion;
    }

    public void setTipoDevolucion(String tipoDevolucion) {
        this.tipoDevolucion = tipoDevolucion;
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

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public BigDecimal getTotalDevuelto() {
        return totalDevuelto;
    }

    public void setTotalDevuelto(BigDecimal totalDevuelto) {
        this.totalDevuelto = totalDevuelto;
    }

    public String getMotivoGeneral() {
        return motivoGeneral;
    }

    public void setMotivoGeneral(String motivoGeneral) {
        this.motivoGeneral = motivoGeneral;
    }

    public String getEstadoDevolucion() {
        return estadoDevolucion;
    }

    public void setEstadoDevolucion(String estadoDevolucion) {
        this.estadoDevolucion = estadoDevolucion;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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
