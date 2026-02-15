package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entidad ZonaDelivery - Zonas de cobertura para delivery
 * Tabla: drinkgo.zona_delivery
 */
@Entity
@Table(name = "zona_delivery", schema = "drinkgo")
public class ZonaDelivery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @Column(name = "sede_id", nullable = false)
    private Long sedeId;
    
    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;
    
    @Column(name = "distritos", columnDefinition = "text[]")
    private String[] distritos;
    
    @Column(name = "costo_delivery", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoDelivery = BigDecimal.ZERO;
    
    @Column(name = "tiempo_estimado_minutos")
    private Integer tiempoEstimadoMinutos;
    
    @Column(name = "pedido_minimo", precision = 12, scale = 2)
    private BigDecimal pedidoMinimo = BigDecimal.ZERO;
    
    @Column(name = "poligono", columnDefinition = "jsonb")
    private String poligono; // GeoJSON
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
    
    @PrePersist
    protected void onCreate() {
        creadoEn = OffsetDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String[] getDistritos() { return distritos; }
    public void setDistritos(String[] distritos) { this.distritos = distritos; }
    
    public BigDecimal getCostoDelivery() { return costoDelivery; }
    public void setCostoDelivery(BigDecimal costoDelivery) { this.costoDelivery = costoDelivery; }
    
    public Integer getTiempoEstimadoMinutos() { return tiempoEstimadoMinutos; }
    public void setTiempoEstimadoMinutos(Integer tiempoEstimadoMinutos) { this.tiempoEstimadoMinutos = tiempoEstimadoMinutos; }
    
    public BigDecimal getPedidoMinimo() { return pedidoMinimo; }
    public void setPedidoMinimo(BigDecimal pedidoMinimo) { this.pedidoMinimo = pedidoMinimo; }
    
    public String getPoligono() { return poligono; }
    public void setPoligono(String poligono) { this.poligono = poligono; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public OffsetDateTime getCreadoEn() { return creadoEn; }
}
