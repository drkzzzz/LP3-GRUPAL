package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;

/**
 * Entidad SedeConfig - Configuración operativa de cada sede
 * Tabla: drinkgo.sede_config
 */
@Entity
@Table(name = "sede_config", schema = "drinkgo")
public class SedeConfig {
    
    @Id
    @Column(name = "sede_id")
    private Long sedeId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "sede_id")
    private Sede sede;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    // Horarios de operación
    @Column(name = "hora_apertura")
    private LocalTime horaApertura;
    
    @Column(name = "hora_cierre")
    private LocalTime horaCierre;
    
    // Restricciones de venta de alcohol
    @Column(name = "hora_inicio_venta_alcohol")
    private LocalTime horaInicioVentaAlcohol;
    
    @Column(name = "hora_fin_venta_alcohol")
    private LocalTime horaFinVentaAlcohol;
    
    @Column(name = "dias_sin_venta_alcohol", columnDefinition = "integer[]")
    private int[] diasSinVentaAlcohol;
    
    @Column(name = "ley_seca_activa", nullable = false)
    private Boolean leySecaActiva = false;
    
    // Configuración de delivery
    @Column(name = "delivery_radio_km", precision = 5, scale = 2)
    private BigDecimal deliveryRadioKm;
    
    @Column(name = "delivery_costo_base", precision = 12, scale = 2)
    private BigDecimal deliveryCostoBase;
    
    @Column(name = "delivery_costo_por_km", precision = 12, scale = 2)
    private BigDecimal deliveryCostoPorKm;
    
    @Column(name = "delivery_pedido_minimo", precision = 12, scale = 2)
    private BigDecimal deliveryPedidoMinimo;
    
    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = OffsetDateTime.now();
    }
    
    // Getters y Setters
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
    
    public Sede getSede() { return sede; }
    public void setSede(Sede sede) { this.sede = sede; }
    
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    
    public LocalTime getHoraApertura() { return horaApertura; }
    public void setHoraApertura(LocalTime horaApertura) { this.horaApertura = horaApertura; }
    
    public LocalTime getHoraCierre() { return horaCierre; }
    public void setHoraCierre(LocalTime horaCierre) { this.horaCierre = horaCierre; }
    
    public LocalTime getHoraInicioVentaAlcohol() { return horaInicioVentaAlcohol; }
    public void setHoraInicioVentaAlcohol(LocalTime horaInicioVentaAlcohol) { this.horaInicioVentaAlcohol = horaInicioVentaAlcohol; }
    
    public LocalTime getHoraFinVentaAlcohol() { return horaFinVentaAlcohol; }
    public void setHoraFinVentaAlcohol(LocalTime horaFinVentaAlcohol) { this.horaFinVentaAlcohol = horaFinVentaAlcohol; }
    
    public int[] getDiasSinVentaAlcohol() { return diasSinVentaAlcohol; }
    public void setDiasSinVentaAlcohol(int[] diasSinVentaAlcohol) { this.diasSinVentaAlcohol = diasSinVentaAlcohol; }
    
    public Boolean getLeySecaActiva() { return leySecaActiva; }
    public void setLeySecaActiva(Boolean leySecaActiva) { this.leySecaActiva = leySecaActiva; }
    
    public BigDecimal getDeliveryRadioKm() { return deliveryRadioKm; }
    public void setDeliveryRadioKm(BigDecimal deliveryRadioKm) { this.deliveryRadioKm = deliveryRadioKm; }
    
    public BigDecimal getDeliveryCostoBase() { return deliveryCostoBase; }
    public void setDeliveryCostoBase(BigDecimal deliveryCostoBase) { this.deliveryCostoBase = deliveryCostoBase; }
    
    public BigDecimal getDeliveryCostoPorKm() { return deliveryCostoPorKm; }
    public void setDeliveryCostoPorKm(BigDecimal deliveryCostoPorKm) { this.deliveryCostoPorKm = deliveryCostoPorKm; }
    
    public BigDecimal getDeliveryPedidoMinimo() { return deliveryPedidoMinimo; }
    public void setDeliveryPedidoMinimo(BigDecimal deliveryPedidoMinimo) { this.deliveryPedidoMinimo = deliveryPedidoMinimo; }
    
    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
}
