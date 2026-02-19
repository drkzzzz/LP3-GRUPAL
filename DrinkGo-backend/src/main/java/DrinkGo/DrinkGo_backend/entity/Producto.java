package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de referencia para productos del catálogo.
 * Solo se mapean campos necesarios para las relaciones del Bloque 5.
 */
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "url_imagen", length = 500)
    private String urlImagen;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    @Column(name = "punto_reorden")
    private Integer puntoReorden;

    @Column(name = "es_perecible", nullable = false)
    private Boolean esPerecible;

    @Column(name = "dias_vida_util")
    private Integer diasVidaUtil;

    @Column(name = "precio_compra", nullable = false)
    private BigDecimal precioCompra;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }

    public Integer getPuntoReorden() { return puntoReorden; }
    public void setPuntoReorden(Integer puntoReorden) { this.puntoReorden = puntoReorden; }

    public Boolean getEsPerecible() { return esPerecible; }
    public void setEsPerecible(Boolean esPerecible) { this.esPerecible = esPerecible; }

    public Integer getDiasVidaUtil() { return diasVidaUtil; }
    public void setDiasVidaUtil(Integer diasVidaUtil) { this.diasVidaUtil = diasVidaUtil; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public LocalDateTime getEliminadoEn() { return eliminadoEn; }
    public void setEliminadoEn(LocalDateTime eliminadoEn) { this.eliminadoEn = eliminadoEn; }
}
