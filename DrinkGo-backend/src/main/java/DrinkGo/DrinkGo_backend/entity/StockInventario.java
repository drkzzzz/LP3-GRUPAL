package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_inventario")
@SQLDelete(sql = "UPDATE stock_inventario SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class StockInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "almacen_id", nullable = false)
    private Long almacenId;

    @Column(name = "cantidad_en_mano", nullable = false)
    private Integer cantidadEnMano = 0;

    @Column(name = "cantidad_reservada", nullable = false)
    private Integer cantidadReservada = 0;

    // Columna generada por MySQL: cantidad_en_mano - cantidad_reservada
    @Column(name = "cantidad_disponible", insertable = false, updatable = false)
    private Integer cantidadDisponible;

    @Column(name = "ultimo_conteo_en")
    private LocalDateTime ultimoConteoEn;

    @Column(name = "ultimo_movimiento_en")
    private LocalDateTime ultimoMovimientoEn;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    // --- Getters y Setters ---

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

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getAlmacenId() {
        return almacenId;
    }

    public void setAlmacenId(Long almacenId) {
        this.almacenId = almacenId;
    }

    public Integer getCantidadEnMano() {
        return cantidadEnMano;
    }

    public void setCantidadEnMano(Integer cantidadEnMano) {
        this.cantidadEnMano = cantidadEnMano;
    }

    public Integer getCantidadReservada() {
        return cantidadReservada;
    }

    public void setCantidadReservada(Integer cantidadReservada) {
        this.cantidadReservada = cantidadReservada;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public LocalDateTime getUltimoConteoEn() {
        return ultimoConteoEn;
    }

    public void setUltimoConteoEn(LocalDateTime ultimoConteoEn) {
        this.ultimoConteoEn = ultimoConteoEn;
    }

    public LocalDateTime getUltimoMovimientoEn() {
        return ultimoMovimientoEn;
    }

    public void setUltimoMovimientoEn(LocalDateTime ultimoMovimientoEn) {
        this.ultimoMovimientoEn = ultimoMovimientoEn;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public LocalDateTime getEliminadoEn() {
        return eliminadoEn;
    }

    public void setEliminadoEn(LocalDateTime eliminadoEn) {
        this.eliminadoEn = eliminadoEn;
    }
}
