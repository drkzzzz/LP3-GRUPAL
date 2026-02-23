package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ZonasDeliveryDTO {

    private Long id;
    private Long negocioId;
    private String nombre;
    private String coordenadasPoligono;
    private BigDecimal costoEnvio;
    private Integer tiempoEstimadoMinutos;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private LocalDateTime eliminadoEn;

    // Constructores
    public ZonasDeliveryDTO() {
    }

    public ZonasDeliveryDTO(Long id, Long negocioId, String nombre, String coordenadasPoligono,
            BigDecimal costoEnvio, Integer tiempoEstimadoMinutos, Boolean estaActivo, LocalDateTime creadoEn,
            LocalDateTime actualizadoEn, LocalDateTime eliminadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.nombre = nombre;
        this.coordenadasPoligono = coordenadasPoligono;
        this.costoEnvio = costoEnvio;
        this.tiempoEstimadoMinutos = tiempoEstimadoMinutos;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCoordenadasPoligono() {
        return coordenadasPoligono;
    }

    public void setCoordenadasPoligono(String coordenadasPoligono) {
        this.coordenadasPoligono = coordenadasPoligono;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public Integer getTiempoEstimadoMinutos() {
        return tiempoEstimadoMinutos;
    }

    public void setTiempoEstimadoMinutos(Integer tiempoEstimadoMinutos) {
        this.tiempoEstimadoMinutos = tiempoEstimadoMinutos;
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
