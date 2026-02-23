package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientosInventarioDTO {

    private Long id;
    private Long negocioId;
    private Long almacenId;
    private Long productoId;
    private Long loteId;
    private String tipoMovimiento;
    private BigDecimal cantidad;
    private String referencia;
    private Long usuarioId;
    private LocalDateTime creadoEn;

    // Constructores
    public MovimientosInventarioDTO() {
    }

    public MovimientosInventarioDTO(Long id, Long negocioId, Long almacenId, Long productoId, Long loteId,
            String tipoMovimiento, BigDecimal cantidad, String referencia, Long usuarioId, LocalDateTime creadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.almacenId = almacenId;
        this.productoId = productoId;
        this.loteId = loteId;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.referencia = referencia;
        this.usuarioId = usuarioId;
        this.creadoEn = creadoEn;
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

    public Long getAlmacenId() {
        return almacenId;
    }

    public void setAlmacenId(Long almacenId) {
        this.almacenId = almacenId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
