package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SuscripcionesDTO {

    private Long id;
    private Long negocioId;
    private Long planSuscripcionId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estadoSuscripcion;
    private Boolean renovacionAutomatica;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public SuscripcionesDTO() {
    }

    public SuscripcionesDTO(Long id, Long negocioId, Long planSuscripcionId, LocalDate fechaInicio,
            LocalDate fechaFin, String estadoSuscripcion, Boolean renovacionAutomatica, LocalDateTime creadoEn,
            LocalDateTime actualizadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.planSuscripcionId = planSuscripcionId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estadoSuscripcion = estadoSuscripcion;
        this.renovacionAutomatica = renovacionAutomatica;
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

    public Long getPlanSuscripcionId() {
        return planSuscripcionId;
    }

    public void setPlanSuscripcionId(Long planSuscripcionId) {
        this.planSuscripcionId = planSuscripcionId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstadoSuscripcion() {
        return estadoSuscripcion;
    }

    public void setEstadoSuscripcion(String estadoSuscripcion) {
        this.estadoSuscripcion = estadoSuscripcion;
    }

    public Boolean getRenovacionAutomatica() {
        return renovacionAutomatica;
    }

    public void setRenovacionAutomatica(Boolean renovacionAutomatica) {
        this.renovacionAutomatica = renovacionAutomatica;
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
