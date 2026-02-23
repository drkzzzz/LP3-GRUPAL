package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class MarcasDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String urlImagen;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public MarcasDTO() {
    }

    public MarcasDTO(Long id, String nombre, String descripcion, String urlImagen, Boolean estaActivo,
            LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.urlImagen = urlImagen;
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

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
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
