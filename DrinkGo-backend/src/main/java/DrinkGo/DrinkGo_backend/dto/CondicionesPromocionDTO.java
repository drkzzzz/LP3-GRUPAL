package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CondicionesPromocionDTO {

    private Long id;
    private Long promocionId;
    private String tipoCondicion;
    private BigDecimal valorMinimo;
    private Integer cantidadMinima;
    private Long productoId;
    private Long categoriaId;
    private LocalDateTime creadoEn;

    // Constructores
    public CondicionesPromocionDTO() {
    }

    public CondicionesPromocionDTO(Long id, Long promocionId, String tipoCondicion, BigDecimal valorMinimo,
            Integer cantidadMinima, Long productoId, Long categoriaId, LocalDateTime creadoEn) {
        this.id = id;
        this.promocionId = promocionId;
        this.tipoCondicion = tipoCondicion;
        this.valorMinimo = valorMinimo;
        this.cantidadMinima = cantidadMinima;
        this.productoId = productoId;
        this.categoriaId = categoriaId;
        this.creadoEn = creadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPromocionId() {
        return promocionId;
    }

    public void setPromocionId(Long promocionId) {
        this.promocionId = promocionId;
    }

    public String getTipoCondicion() {
        return tipoCondicion;
    }

    public void setTipoCondicion(String tipoCondicion) {
        this.tipoCondicion = tipoCondicion;
    }

    public BigDecimal getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public Integer getCantidadMinima() {
        return cantidadMinima;
    }

    public void setCantidadMinima(Integer cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
