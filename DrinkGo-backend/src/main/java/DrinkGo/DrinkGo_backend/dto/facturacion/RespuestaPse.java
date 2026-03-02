package DrinkGo.DrinkGo_backend.dto.facturacion;

import java.time.LocalDateTime;

/**
 * DTO que representa la respuesta de un Proveedor de Servicios
 * Electrónicos (PSE) tras enviar un documento a SUNAT.
 */
public class RespuestaPse {
    private boolean aceptado;
    private String codigoRespuesta;
    private String mensajeRespuesta;
    private String hashCdr;
    private String xmlGenerado;
    private String observaciones;
    private String firmaDigital;
    private String cdrBase64;
    private LocalDateTime fechaRespuesta;

    public boolean isAceptado() { return aceptado; }
    public void setAceptado(boolean aceptado) { this.aceptado = aceptado; }

    public String getCodigoRespuesta() { return codigoRespuesta; }
    public void setCodigoRespuesta(String codigoRespuesta) { this.codigoRespuesta = codigoRespuesta; }

    public String getMensajeRespuesta() { return mensajeRespuesta; }
    public void setMensajeRespuesta(String mensajeRespuesta) { this.mensajeRespuesta = mensajeRespuesta; }

    public String getHashCdr() { return hashCdr; }
    public void setHashCdr(String hashCdr) { this.hashCdr = hashCdr; }

    public String getXmlGenerado() { return xmlGenerado; }
    public void setXmlGenerado(String xmlGenerado) { this.xmlGenerado = xmlGenerado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getFirmaDigital() { return firmaDigital; }
    public void setFirmaDigital(String firmaDigital) { this.firmaDigital = firmaDigital; }

    public String getCdrBase64() { return cdrBase64; }
    public void setCdrBase64(String cdrBase64) { this.cdrBase64 = cdrBase64; }

    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }

    @Override
    public String toString() {
        return "RespuestaPse{" +
                "aceptado=" + aceptado +
                ", codigo='" + codigoRespuesta + '\'' +
                ", mensaje='" + mensajeRespuesta + '\'' +
                ", hash='" + hashCdr + '\'' +
                '}';
    }
}
