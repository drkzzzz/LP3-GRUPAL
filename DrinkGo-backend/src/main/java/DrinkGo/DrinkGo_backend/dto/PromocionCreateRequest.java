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
    private String descripcion;
    private String tipoDescuento;       // porcentaje, monto_fijo, compre_x_lleve_y, envio_gratis
    private BigDecimal valorDescuento;
    private BigDecimal montoMinimoCompra;
    private BigDecimal montoMaximoDescuento;
    private Integer maxUsos;
    private Integer maxUsosPorCliente;
    private String aplicaA;             // todo, categoria, producto, marca, combo
    private LocalDateTime validoDesde;
    private LocalDateTime validoHasta;
    private Boolean esCombinable;
    private String canales;             // JSON string: ["pos","tienda_online"]
    private List<CondicionPromocionRequest> condiciones;
    private List<Long> sedeIds;

    // --- Getters y Setters ---

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipoDescuento() { return tipoDescuento; }
    public void setTipoDescuento(String tipoDescuento) { this.tipoDescuento = tipoDescuento; }

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

    public String getAplicaA() { return aplicaA; }
    public void setAplicaA(String aplicaA) { this.aplicaA = aplicaA; }

    public LocalDateTime getValidoDesde() { return validoDesde; }
    public void setValidoDesde(LocalDateTime validoDesde) { this.validoDesde = validoDesde; }

    public LocalDateTime getValidoHasta() { return validoHasta; }
    public void setValidoHasta(LocalDateTime validoHasta) { this.validoHasta = validoHasta; }

    public Boolean getEsCombinable() { return esCombinable; }
    public void setEsCombinable(Boolean esCombinable) { this.esCombinable = esCombinable; }

    public String getCanales() { return canales; }
    public void setCanales(String canales) { this.canales = canales; }

    public List<CondicionPromocionRequest> getCondiciones() { return condiciones; }
    public void setCondiciones(List<CondicionPromocionRequest> condiciones) { this.condiciones = condiciones; }

    public List<Long> getSedeIds() { return sedeIds; }
    public void setSedeIds(List<Long> sedeIds) { this.sedeIds = sedeIds; }
}