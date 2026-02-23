package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class CategoriasGastoDTO {

    private Long id;
    private Long negocioId;
    private String nombre;
    private String descripcion;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public CategoriasGastoDTO() {
    }

    public CategoriasGastoDTO(Long id, Long negocioId, String nombre, String descripcion, Boolean estaActivo,
            LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estaActivo = estaActivo;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
}
