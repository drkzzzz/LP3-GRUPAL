package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para crear una promoción (POST /restful/promociones).
 * Bloque 13.
 */
public class PromocionCreateRequest {

    private String nombre;
    private String codigo;
    private String tipoDescuento;       // porcentaje, monto_fijo
    private BigDecimal valorDescuento;
    private BigDecimal montoMinimoCompra;
    private Integer maxUsos;
    private String aplicaA;             // todo, categoria, producto
    private LocalDateTime validoDesde;
    private LocalDateTime validoHasta;
    private List<CondicionPromocionRequest> condiciones;

    // --- Getters y Setters ---

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTipoDescuento() { return tipoDescuento; }
    public void setTipoDescuento(String tipoDescuento) { this.tipoDescuento = tipoDescuento; }

    public BigDecimal getValorDescuento() { return valorDescuento; }
    public void setValorDescuento(BigDecimal valorDescuento) { this.valorDescuento = valorDescuento; }

    public BigDecimal getMontoMinimoCompra() { return montoMinimoCompra; }
    public void setMontoMinimoCompra(BigDecimal montoMinimoCompra) { this.montoMinimoCompra = montoMinimoCompra; }

    public Integer getMaxUsos() { return maxUsos; }
    public void setMaxUsos(Integer maxUsos) { this.maxUsos = maxUsos; }

    public String getAplicaA() { return aplicaA; }
    public void setAplicaA(String aplicaA) { this.aplicaA = aplicaA; }

    public LocalDateTime getValidoDesde() { return validoDesde; }
    public void setValidoDesde(LocalDateTime validoDesde) { this.validoDesde = validoDesde; }

    public LocalDateTime getValidoHasta() { return validoHasta; }
    public void setValidoHasta(LocalDateTime validoHasta) { this.validoHasta = validoHasta; }

    public List<CondicionPromocionRequest> getCondiciones() { return condiciones; }
    public void setCondiciones(List<CondicionPromocionRequest> condiciones) { this.condiciones = condiciones; }
}
