package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class CajasRegistradorasDTO {

    private Long id;
    private Long negocioId;
    private Long sedeId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Boolean estaActiva;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public CajasRegistradorasDTO() {
    }

    public CajasRegistradorasDTO(Long id, Long negocioId, Long sedeId, String codigo, String nombre,
            String descripcion, Boolean estaActiva, Boolean estaActivo, LocalDateTime creadoEn,
            LocalDateTime actualizadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.sedeId = sedeId;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estaActiva = estaActiva;
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

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
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

    public Boolean getEstaActiva() {
        return estaActiva;
    }

    public void setEstaActiva(Boolean estaActiva) {
        this.estaActiva = estaActiva;
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
