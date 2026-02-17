package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Request para crear un documento de facturación electrónica.
 */
public class CreateDocumentoFacturacionRequest {

    private Long negocioId;
    private Long sedeId;
    private Long serieId;
    private Long ventaId;
    private Long pedidoId;
    private String tipoDocumento; // boleta, factura, nota_credito, nota_debito, guia_remision

    // --- Emisor (snapshot del negocio) ---
    private String rucEmisor;
    private String razonSocialEmisor;
    private String direccionEmisor;

    // --- Receptor ---
    private String tipoDocumentoReceptor; // 1=DNI, 6=RUC, etc.
    private String numeroDocumentoReceptor;
    private String nombreReceptor;
    private String direccionReceptor;
    private String emailReceptor;

    // --- Montos opcionales (se pueden calcular) ---
    private String moneda;
    private BigDecimal tipoCambio;
    private BigDecimal totalDescuento;

    // --- Fechas ---
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;

    // --- Referencia para notas crédito/débito ---
    private Long documentoReferenciadoId;
    private String motivoReferencia;

    // --- Creador ---
    private Long creadoPor;

    // --- Detalles (items) ---
    private List<DetalleDocumentoFacturacionRequest> detalles;

    // --- Getters y Setters ---

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    public Long getSerieId() { return serieId; }
    public void setSerieId(Long serieId) { this.serieId = serieId; }

    public Long getVentaId() { return ventaId; }
    public void setVentaId(Long ventaId) { this.ventaId = ventaId; }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getRucEmisor() { return rucEmisor; }
    public void setRucEmisor(String rucEmisor) { this.rucEmisor = rucEmisor; }

    public String getRazonSocialEmisor() { return razonSocialEmisor; }
    public void setRazonSocialEmisor(String razonSocialEmisor) { this.razonSocialEmisor = razonSocialEmisor; }

    public String getDireccionEmisor() { return direccionEmisor; }
    public void setDireccionEmisor(String direccionEmisor) { this.direccionEmisor = direccionEmisor; }

    public String getTipoDocumentoReceptor() { return tipoDocumentoReceptor; }
    public void setTipoDocumentoReceptor(String tipoDocumentoReceptor) { this.tipoDocumentoReceptor = tipoDocumentoReceptor; }

    public String getNumeroDocumentoReceptor() { return numeroDocumentoReceptor; }
    public void setNumeroDocumentoReceptor(String numeroDocumentoReceptor) { this.numeroDocumentoReceptor = numeroDocumentoReceptor; }

    public String getNombreReceptor() { return nombreReceptor; }
    public void setNombreReceptor(String nombreReceptor) { this.nombreReceptor = nombreReceptor; }

    public String getDireccionReceptor() { return direccionReceptor; }
    public void setDireccionReceptor(String direccionReceptor) { this.direccionReceptor = direccionReceptor; }

    public String getEmailReceptor() { return emailReceptor; }
    public void setEmailReceptor(String emailReceptor) { this.emailReceptor = emailReceptor; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public BigDecimal getTipoCambio() { return tipoCambio; }
    public void setTipoCambio(BigDecimal tipoCambio) { this.tipoCambio = tipoCambio; }

    public BigDecimal getTotalDescuento() { return totalDescuento; }
    public void setTotalDescuento(BigDecimal totalDescuento) { this.totalDescuento = totalDescuento; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Long getDocumentoReferenciadoId() { return documentoReferenciadoId; }
    public void setDocumentoReferenciadoId(Long documentoReferenciadoId) { this.documentoReferenciadoId = documentoReferenciadoId; }

    public String getMotivoReferencia() { return motivoReferencia; }
    public void setMotivoReferencia(String motivoReferencia) { this.motivoReferencia = motivoReferencia; }

    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }

    public List<DetalleDocumentoFacturacionRequest> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleDocumentoFacturacionRequest> detalles) { this.detalles = detalles; }
}
