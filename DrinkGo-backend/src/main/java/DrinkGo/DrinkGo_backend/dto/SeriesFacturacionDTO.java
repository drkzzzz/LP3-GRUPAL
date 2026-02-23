package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class SeriesFacturacionDTO {

    private Long id;
    private Long negocioId;
    private Long sedeId;
    private String tipoDocumento;
    private String serie;
    private Integer numeroActual;
    private Boolean estaActiva;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public SeriesFacturacionDTO() {
    }

    public SeriesFacturacionDTO(Long id, Long negocioId, Long sedeId, String tipoDocumento, String serie,
            Integer numeroActual, Boolean estaActiva, LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.sedeId = sedeId;
        this.tipoDocumento = tipoDocumento;
        this.serie = serie;
        this.numeroActual = numeroActual;
        this.estaActiva = estaActiva;
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

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Integer getNumeroActual() {
        return numeroActual;
    }

    public void setNumeroActual(Integer numeroActual) {
        this.numeroActual = numeroActual;
    }

    public Boolean getEstaActiva() {
        return estaActiva;
    }

    public void setEstaActiva(Boolean estaActiva) {
        this.estaActiva = estaActiva;
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
