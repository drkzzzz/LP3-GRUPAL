package DrinkGo.DrinkGo_backend.dto.facturacion;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para emitir una Nota de Crédito o Nota de Débito.
 * <p>
 * Códigos de motivo SUNAT para Nota de Crédito:
 *   01 = Anulación de la operación
 *   02 = Anulación por error en el RUC
 *   03 = Corrección por error en la descripción
 *   04 = Descuento global
 *   06 = Devolución total
 *   07 = Devolución por ítem
 *   09 = Disminución en el valor
 * <p>
 * Códigos de motivo SUNAT para Nota de Débito:
 *   01 = Intereses por mora
 *   02 = Aumento en el valor
 *   03 = Penalidades / otros conceptos
 * <p>
 * Comportamiento según motivo:
 * - 01, 02, 03, 06 (NC): Anulación completa → copia todos los ítems del original.
 * - 07 (NC): Devolución parcial → requiere {@code items} con productos y cantidades.
 * - 04, 09 (NC): Descuento/disminución → requiere {@code monto}.
 * - ND (01, 02, 03): Cargo adicional → requiere {@code monto}.
 */
public class CrearNotaCreditoRequest {

    /** ID del comprobante original (boleta o factura) */
    private Long documentoReferenciaId;

    /** "nota_credito" o "nota_debito" */
    private String tipoNota;

    /** Código de motivo SUNAT (ej: "01", "06") */
    private String codigoMotivo;

    /** Descripción libre del motivo */
    private String descripcionMotivo;

    /** ID del usuario que emite */
    private Long usuarioId;

    /**
     * Para motivo 07 (devolución por ítem): lista de ítems a devolver.
     * Cada ítem tiene productoId (o comboId) y cantidad a devolver.
     */
    private List<ItemDevolucion> items;

    /**
     * Para motivos 04/09 (descuento/disminución) y todos los ND:
     * monto del ajuste (positivo). El sistema calcula sub/igv/total.
     */
    private BigDecimal monto;

    /**
     * Indica si el usuario desea registrar la devolución de dinero al cliente.
     * Si es true y la sesión de caja está abierta, se creará un egreso.
     * Si es false o null, la NC se crea sin movimiento de caja.
     * Solo aplica para notas de crédito.
     */
    private Boolean devolverDinero;

    // ─── Getters / Setters ───

    public Long getDocumentoReferenciaId() { return documentoReferenciaId; }
    public void setDocumentoReferenciaId(Long documentoReferenciaId) { this.documentoReferenciaId = documentoReferenciaId; }

    public String getTipoNota() { return tipoNota; }
    public void setTipoNota(String tipoNota) { this.tipoNota = tipoNota; }

    public String getCodigoMotivo() { return codigoMotivo; }
    public void setCodigoMotivo(String codigoMotivo) { this.codigoMotivo = codigoMotivo; }

    public String getDescripcionMotivo() { return descripcionMotivo; }
    public void setDescripcionMotivo(String descripcionMotivo) { this.descripcionMotivo = descripcionMotivo; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public List<ItemDevolucion> getItems() { return items; }
    public void setItems(List<ItemDevolucion> items) { this.items = items; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public Boolean getDevolverDinero() { return devolverDinero; }
    public void setDevolverDinero(Boolean devolverDinero) { this.devolverDinero = devolverDinero; }

    // ─── Inner class para ítems de devolución parcial ───

    public static class ItemDevolucion {
        private Long productoId;
        private Long comboId;
        private BigDecimal cantidad;

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public Long getComboId() { return comboId; }
        public void setComboId(Long comboId) { this.comboId = comboId; }

        public BigDecimal getCantidad() { return cantidad; }
        public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    }
}
