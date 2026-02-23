package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class PermisosSistemaDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Long moduloSistemaId;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public PermisosSistemaDTO() {
    }

    public PermisosSistemaDTO(Long id, String codigo, String nombre, String descripcion, Long moduloSistemaId,
            Boolean estaActivo, LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.moduloSistemaId = moduloSistemaId;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public Long getModuloSistemaId() {
        return moduloSistemaId;
    }

    public void setModuloSistemaId(Long moduloSistemaId) {
        this.moduloSistemaId = moduloSistemaId;
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
