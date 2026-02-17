package DrinkGo.DrinkGo_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad Promocion - Mapea la tabla 'promociones' de drinkgo_database.sql.
 * Bloque 13: Descuentos y Promociones (RF-PRO-015).
 * Borrado lógico: esta_activo = 0 vía @SQLDelete.
 */
@Entity
@Table(name = "promociones")
@SQLDelete(sql = "UPDATE promociones SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
public class Promocion {

    public enum TipoDescuento {
        porcentaje, monto_fijo, compre_x_lleve_y, envio_gratis
    }

    public enum AplicaA {
        todo, categoria, producto, marca, combo
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "codigo", length = 50)
    private String codigo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descuento", nullable = false)
    private TipoDescuento tipoDescuento;

    @Column(name = "valor_descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDescuento;

    @Column(name = "monto_minimo_compra", precision = 10, scale = 2)
    private BigDecimal montoMinimoCompra;

    @Column(name = "monto_maximo_descuento", precision = 10, scale = 2)
    private BigDecimal montoMaximoDescuento;

    @Column(name = "max_usos")
    private Integer maxUsos;

    @Column(name = "max_usos_por_cliente")
    private Integer maxUsosPorCliente;

    @Column(name = "usos_actuales", nullable = false)
    private Integer usosActuales = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "aplica_a", nullable = false)
    private AplicaA aplicaA = AplicaA.todo;

    @Column(name = "valido_desde", nullable = false)
    private LocalDateTime validoDesde;

    @Column(name = "valido_hasta", nullable = false)
    private LocalDateTime validoHasta;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "es_combinable", nullable = false)
    private Boolean esCombinable = false;

    @Column(name = "canales", columnDefinition = "JSON")
    private String canales;

    @Column(name = "creado_por")
    private Long creadoPor;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoDescuento getTipoDescuento() { return tipoDescuento; }
    public void setTipoDescuento(TipoDescuento tipoDescuento) { this.tipoDescuento = tipoDescuento; }

    public BigDecimal getValorDescuento() { return valorDescuento; }
    public void setValorDescuento(BigDecimal valorDescuento) { this.valorDescuento = valorDescuento; }

    public BigDecimal getMontoMinimoCompra() { return montoMinimoCompra; }
    public void setMontoMinimoCompra(BigDecimal montoMinimoCompra) { this.montoMinimoCompra = montoMinimoCompra; }

    public BigDecimal getMontoMaximoDescuento() { return montoMaximoDescuento; }
    public void setMontoMaximoDescuento(BigDecimal montoMaximoDescuento) { this.montoMaximoDescuento = montoMaximoDescuento; }

    public Integer getMaxUsos() { return maxUsos; }
    public void setMaxUsos(Integer maxUsos) { this.maxUsos = maxUsos; }

    public Integer getMaxUsosPorCliente() { return maxUsosPorCliente; }
    public void setMaxUsosPorCliente(Integer maxUsosPorCliente) { this.maxUsosPorCliente = maxUsosPorCliente; }

    public Integer getUsosActuales() { return usosActuales; }
    public void setUsosActuales(Integer usosActuales) { this.usosActuales = usosActuales; }

    public AplicaA getAplicaA() { return aplicaA; }
    public void setAplicaA(AplicaA aplicaA) { this.aplicaA = aplicaA; }

    public LocalDateTime getValidoDesde() { return validoDesde; }
    public void setValidoDesde(LocalDateTime validoDesde) { this.validoDesde = validoDesde; }

    public LocalDateTime getValidoHasta() { return validoHasta; }
    public void setValidoHasta(LocalDateTime validoHasta) { this.validoHasta = validoHasta; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public Boolean getEsCombinable() { return esCombinable; }
    public void setEsCombinable(Boolean esCombinable) { this.esCombinable = esCombinable; }

    public String getCanales() { return canales; }
    public void setCanales(String canales) { this.canales = canales; }

    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}