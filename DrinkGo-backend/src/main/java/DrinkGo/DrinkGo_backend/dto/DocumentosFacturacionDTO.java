package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DocumentosFacturacionDTO {

    private Long id;
    private Long negocioId;
    private Long sedeId;
    private Long serieFacturacionId;
    private String tipoDocumento;
    private String serie;
    private String numero;
    private Long clienteId;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaVencimiento;
    private String tipoMoneda;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal igv;
    private BigDecimal total;
    private String estadoDocumento;
    private String hash;
    private String qr;
    private String xmlUrl;
    private String pdfUrl;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private LocalDateTime eliminadoEn;

    // Constructores
    public DocumentosFacturacionDTO() {
    }

    public DocumentosFacturacionDTO(Long id, Long negocioId, Long sedeId, Long serieFacturacionId,
            String tipoDocumento, String serie, String numero, Long clienteId, LocalDateTime fechaEmision,
            LocalDateTime fechaVencimiento, String tipoMoneda, BigDecimal subtotal, BigDecimal descuento,
            BigDecimal igv, BigDecimal total, String estadoDocumento, String hash, String qr, String xmlUrl,
            String pdfUrl, Boolean estaActivo, LocalDateTime creadoEn, LocalDateTime actualizadoEn,
            LocalDateTime eliminadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.sedeId = sedeId;
        this.serieFacturacionId = serieFacturacionId;
        this.tipoDocumento = tipoDocumento;
        this.serie = serie;
        this.numero = numero;
        this.clienteId = clienteId;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.tipoMoneda = tipoMoneda;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.igv = igv;
        this.total = total;
        this.estadoDocumento = estadoDocumento;
        this.hash = hash;
        this.qr = qr;
        this.xmlUrl = xmlUrl;
        this.pdfUrl = pdfUrl;
        this.estaActivo = estaActivo;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
        this.eliminadoEn = eliminadoEn;
    }

    // Getters y Setters
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

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
    }

    public Long getSerieFacturacionId() {
        return serieFacturacionId;
    }

    public void setSerieFacturacionId(Long serieFacturacionId) {
        this.serieFacturacionId = serieFacturacionId;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstadoDocumento() {
        return estadoDocumento;
    }

    public void setEstadoDocumento(String estadoDocumento) {
        this.estadoDocumento = estadoDocumento;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getXmlUrl() {
        return xmlUrl;
    }

    public void setXmlUrl(String xmlUrl) {
        this.xmlUrl = xmlUrl;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
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
}
