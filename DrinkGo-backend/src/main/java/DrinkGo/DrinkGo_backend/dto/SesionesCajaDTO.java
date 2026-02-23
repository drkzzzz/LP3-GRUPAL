package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SesionesCajaDTO {

    private Long id;
    private Long cajaRegistradoraId;
    private Long usuarioId;
    private LocalDateTime apertura;
    private LocalDateTime cierre;
    private BigDecimal montoInicial;
    private BigDecimal montoFinal;
    private BigDecimal totalVentas;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private Boolean estaAbierta;
    private String observaciones;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public SesionesCajaDTO() {
    }

    public SesionesCajaDTO(Long id, Long cajaRegistradoraId, Long usuarioId, LocalDateTime apertura,
            LocalDateTime cierre, BigDecimal montoInicial, BigDecimal montoFinal, BigDecimal totalVentas,
            BigDecimal totalIngresos, BigDecimal totalEgresos, Boolean estaAbierta, String observaciones,
            LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.cajaRegistradoraId = cajaRegistradoraId;
        this.usuarioId = usuarioId;
        this.apertura = apertura;
        this.cierre = cierre;
        this.montoInicial = montoInicial;
        this.montoFinal = montoFinal;
        this.totalVentas = totalVentas;
        this.totalIngresos = totalIngresos;
        this.totalEgresos = totalEgresos;
        this.estaAbierta = estaAbierta;
        this.observaciones = observaciones;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCajaRegistradoraId() {
        return cajaRegistradoraId;
    }

    public void setCajaRegistradoraId(Long cajaRegistradoraId) {
        this.cajaRegistradoraId = cajaRegistradoraId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getApertura() {
        return apertura;
    }

    public void setApertura(LocalDateTime apertura) {
        this.apertura = apertura;
    }

    public LocalDateTime getCierre() {
        return cierre;
    }

    public void setCierre(LocalDateTime cierre) {
        this.cierre = cierre;
    }

    public BigDecimal getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(BigDecimal montoInicial) {
        this.montoInicial = montoInicial;
    }

    public BigDecimal getMontoFinal() {
        return montoFinal;
    }

    public void setMontoFinal(BigDecimal montoFinal) {
        this.montoFinal = montoFinal;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public BigDecimal getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(BigDecimal totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public BigDecimal getTotalEgresos() {
        return totalEgresos;
    }

    public void setTotalEgresos(BigDecimal totalEgresos) {
        this.totalEgresos = totalEgresos;
    }

    public Boolean getEstaAbierta() {
        return estaAbierta;
    }

    public void setEstaAbierta(Boolean estaAbierta) {
        this.estaAbierta = estaAbierta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
}
