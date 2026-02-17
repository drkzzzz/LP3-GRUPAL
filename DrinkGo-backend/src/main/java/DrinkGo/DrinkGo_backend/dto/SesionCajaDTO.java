package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para sesiones de caja.
 * Bloque 8 - Ventas, POS y Cajas.
 */
public class SesionCajaDTO {

    private Long id;
    private Long negocioId;
    private Long cajaId;
    private String cajaNombre;
    private Long usuarioAperturaId;
    private String usuarioAperturaNombre;
    private Long usuarioCierreId;
    private String usuarioCierreNombre;
    private BigDecimal montoInicial;
    private BigDecimal montoFinal;
    private BigDecimal montoEsperado;
    private BigDecimal diferencia;
    private String observacionesApertura;
    private String observacionesCierre;
    private Boolean estaAbierta;
    private LocalDateTime abiertaEn;
    private LocalDateTime cerradaEn;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    public SesionCajaDTO() {
    }

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

    public Long getCajaId() {
        return cajaId;
    }

    public void setCajaId(Long cajaId) {
        this.cajaId = cajaId;
    }

    public String getCajaNombre() {
        return cajaNombre;
    }

    public void setCajaNombre(String cajaNombre) {
        this.cajaNombre = cajaNombre;
    }

    public Long getUsuarioAperturaId() {
        return usuarioAperturaId;
    }

    public void setUsuarioAperturaId(Long usuarioAperturaId) {
        this.usuarioAperturaId = usuarioAperturaId;
    }

    public String getUsuarioAperturaNombre() {
        return usuarioAperturaNombre;
    }

    public void setUsuarioAperturaNombre(String usuarioAperturaNombre) {
        this.usuarioAperturaNombre = usuarioAperturaNombre;
    }

    public Long getUsuarioCierreId() {
        return usuarioCierreId;
    }

    public void setUsuarioCierreId(Long usuarioCierreId) {
        this.usuarioCierreId = usuarioCierreId;
    }

    public String getUsuarioCierreNombre() {
        return usuarioCierreNombre;
    }

    public void setUsuarioCierreNombre(String usuarioCierreNombre) {
        this.usuarioCierreNombre = usuarioCierreNombre;
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

    public BigDecimal getMontoEsperado() {
        return montoEsperado;
    }

    public void setMontoEsperado(BigDecimal montoEsperado) {
        this.montoEsperado = montoEsperado;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
    }

    public String getObservacionesApertura() {
        return observacionesApertura;
    }

    public void setObservacionesApertura(String observacionesApertura) {
        this.observacionesApertura = observacionesApertura;
    }

    public String getObservacionesCierre() {
        return observacionesCierre;
    }

    public void setObservacionesCierre(String observacionesCierre) {
        this.observacionesCierre = observacionesCierre;
    }

    public Boolean getEstaAbierta() {
        return estaAbierta;
    }

    public void setEstaAbierta(Boolean estaAbierta) {
        this.estaAbierta = estaAbierta;
    }

    public LocalDateTime getAbiertaEn() {
        return abiertaEn;
    }

    public void setAbiertaEn(LocalDateTime abiertaEn) {
        this.abiertaEn = abiertaEn;
    }

    public LocalDateTime getCerradaEn() {
        return cerradaEn;
    }

    public void setCerradaEn(LocalDateTime cerradaEn) {
        this.cerradaEn = cerradaEn;
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
