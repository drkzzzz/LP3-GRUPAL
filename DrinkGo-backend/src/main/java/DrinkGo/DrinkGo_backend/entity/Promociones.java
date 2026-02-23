package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promociones")
@SQLDelete(sql = "UPDATE promociones SET esta_activo = 0, eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "nombre", "codigo", "tipoDescuento", "valorDescuento", "montoMinimoCompra",
        "maxUsos", "usosActuales", "aplicaA", "validoDesde", "validoHasta", "estaActivo", "creadoPor", "creadoEn",
        "actualizadoEn", "eliminadoEn" })
public class Promociones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 50)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descuento", nullable = false)
    private TipoDescuento tipoDescuento;

    @Column(name = "valor_descuento", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorDescuento = BigDecimal.ZERO;

    @Column(name = "monto_minimo_compra", precision = 10, scale = 2)
    private BigDecimal montoMinimoCompra;

    @Column(name = "max_usos")
    private Integer maxUsos;

    @Column(name = "usos_actuales", nullable = false)
    private Integer usosActuales = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "aplica_a", nullable = false)
    private AplicaA aplicaA = AplicaA.todo;

    @Column(name = "valido_desde", nullable = false)
    private LocalDateTime validoDesde;

    @Column(name = "valido_hasta", nullable = false)
    private LocalDateTime validoHasta;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuarios creadoPor;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    public enum TipoDescuento {
        porcentaje, monto_fijo
    }

    public enum AplicaA {
        todo, categoria, producto
    }

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Negocios getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocios negocio) {
        this.negocio = negocio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoDescuento getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(TipoDescuento tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public BigDecimal getValorDescuento() {
        return valorDescuento;
    }

    public void setValorDescuento(BigDecimal valorDescuento) {
        this.valorDescuento = valorDescuento;
    }

    public BigDecimal getMontoMinimoCompra() {
        return montoMinimoCompra;
    }

    public void setMontoMinimoCompra(BigDecimal montoMinimoCompra) {
        this.montoMinimoCompra = montoMinimoCompra;
    }

    public Integer getMaxUsos() {
        return maxUsos;
    }

    public void setMaxUsos(Integer maxUsos) {
        this.maxUsos = maxUsos;
    }

    public Integer getUsosActuales() {
        return usosActuales;
    }

    public void setUsosActuales(Integer usosActuales) {
        this.usosActuales = usosActuales;
    }

    public AplicaA getAplicaA() {
        return aplicaA;
    }

    public void setAplicaA(AplicaA aplicaA) {
        this.aplicaA = aplicaA;
    }

    public LocalDateTime getValidoDesde() {
        return validoDesde;
    }

    public void setValidoDesde(LocalDateTime validoDesde) {
        this.validoDesde = validoDesde;
    }

    public LocalDateTime getValidoHasta() {
        return validoHasta;
    }

    public void setValidoHasta(LocalDateTime validoHasta) {
        this.validoHasta = validoHasta;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public Usuarios getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Usuarios creadoPor) {
        this.creadoPor = creadoPor;
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

    @Override
    public String toString() {
        return "Promociones [id=" + id + ", nombre=" + nombre + ", codigo=" + codigo + ", tipoDescuento="
                + tipoDescuento + ", valorDescuento=" + valorDescuento + ", estaActivo=" + estaActivo + "]";
    }
}
