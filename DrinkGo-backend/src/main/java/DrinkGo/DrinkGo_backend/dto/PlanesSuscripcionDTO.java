package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PlanesSuscripcionDTO {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String periodoFacturacion;
    private BigDecimal precio;
    private Integer limiteSedes;
    private Integer limiteUsuarios;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public PlanesSuscripcionDTO() {
    }

    public PlanesSuscripcionDTO(Long id, String codigo, String nombre, String descripcion, String periodoFacturacion,
            BigDecimal precio, Integer limiteSedes, Integer limiteUsuarios, Boolean estaActivo,
            LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.periodoFacturacion = periodoFacturacion;
        this.precio = precio;
        this.limiteSedes = limiteSedes;
        this.limiteUsuarios = limiteUsuarios;
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

    public String getPeriodoFacturacion() {
        return periodoFacturacion;
    }

    public void setPeriodoFacturacion(String periodoFacturacion) {
        this.periodoFacturacion = periodoFacturacion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getLimiteSedes() {
        return limiteSedes;
    }

    public void setLimiteSedes(Integer limiteSedes) {
        this.limiteSedes = limiteSedes;
    }

    public Integer getLimiteUsuarios() {
        return limiteUsuarios;
    }

    public void setLimiteUsuarios(Integer limiteUsuarios) {
        this.limiteUsuarios = limiteUsuarios;
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
