package DrinkGo.DrinkGo_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;

import DrinkGo.DrinkGo_backend.entity.DocumentosFacturacion;

/**
 * Simulador del Proveedor de Servicios Electrónicos (PSE) de SUNAT.
 * <p>
 * En producción, este servicio se reemplazaría con la integración real
 * a un PSE como Nubefact, Efact, Bizlinks, etc.
 * <p>
 * Simula:
 * <ul>
 *   <li>Validación estructural del comprobante</li>
 *   <li>Generación de XML UBL 2.1</li>
 *   <li>Envío a SUNAT y recepción de CDR</li>
 *   <li>Respuesta con hash, código y observaciones</li>
 * </ul>
 *
 * La simulación acepta el 95% de los documentos, rechazando
 * aleatoriamente un 5% para simular errores reales de SUNAT.
 */
@Service
public class PseSimuladorService {

    /**
     * Simula el envío de un documento de facturación a SUNAT.
     * <p>
     * Realiza validaciones estructurales y genera una respuesta
     * simulada similar a la que retornaría un PSE real.
     *
     * @param documento El documento a "enviar"
     * @return RespuestaPse con hash, XML, código y mensaje
     */
    public RespuestaPse enviarDocumento(DocumentosFacturacion documento) {
        // 1. Validación estructural
        validarEstructura(documento);

        // 2. Generar XML simulado (UBL 2.1)
        String xmlGenerado = generarXmlSimulado(documento);

        // 3. Simular respuesta SUNAT
        return simularRespuestaSunat(documento, xmlGenerado);
    }

    // ═══════════════════════════════════════════════════════════════════

    private void validarEstructura(DocumentosFacturacion documento) {
        if (documento.getNumeroDocumento() == null || documento.getNumeroDocumento().isEmpty()) {
            throw new IllegalArgumentException("PSE: Número de documento es requerido");
        }
        if (documento.getTotal() == null || documento.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("PSE: Total debe ser mayor a 0");
        }
        if (documento.getCliente() == null) {
            throw new IllegalArgumentException("PSE: Cliente es requerido para emitir comprobante");
        }
        if (documento.getTipoDocumento() == DocumentosFacturacion.TipoDocumento.factura) {
            // Factura requiere RUC (11 dígitos)
            String numDoc = documento.getCliente().getNumeroDocumento();
            if (numDoc == null || numDoc.length() != 11) {
                throw new IllegalArgumentException(
                        "PSE: Factura requiere cliente con RUC de 11 dígitos. Recibido: "
                                + (numDoc != null ? numDoc : "null"));
            }
        }
    }

    private String generarXmlSimulado(DocumentosFacturacion documento) {
        String tipoDoc = documento.getTipoDocumento() == DocumentosFacturacion.TipoDocumento.boleta
                ? "03" : "01";
        String fechaEmision = documento.getFechaEmision() != null
                ? documento.getFechaEmision().format(DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Invoice xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\">\n");
        xml.append("  <UBLVersionID>2.1</UBLVersionID>\n");
        xml.append("  <CustomizationID>2.0</CustomizationID>\n");
        xml.append("  <ID>").append(documento.getNumeroDocumento()).append("</ID>\n");
        xml.append("  <IssueDate>").append(fechaEmision).append("</IssueDate>\n");
        xml.append("  <InvoiceTypeCode>").append(tipoDoc).append("</InvoiceTypeCode>\n");
        xml.append("  <DocumentCurrencyCode>PEN</DocumentCurrencyCode>\n");

        // Emisor (RUC del negocio)
        xml.append("  <AccountingSupplierParty>\n");
        xml.append("    <Party>\n");
        xml.append("      <PartyName><Name>")
                .append(escapeXml(documento.getNegocio() != null
                        ? documento.getNegocio().getRazonSocial() : "NEGOCIO"))
                .append("</Name></PartyName>\n");
        xml.append("    </Party>\n");
        xml.append("  </AccountingSupplierParty>\n");

        // Receptor
        xml.append("  <AccountingCustomerParty>\n");
        xml.append("    <Party>\n");
        if (documento.getCliente() != null) {
            xml.append("      <PartyIdentification><ID>")
                    .append(documento.getCliente().getNumeroDocumento())
                    .append("</ID></PartyIdentification>\n");
            String nombreCliente = documento.getCliente().getRazonSocial() != null
                    ? documento.getCliente().getRazonSocial()
                    : (documento.getCliente().getNombres() + " " + documento.getCliente().getApellidos());
            xml.append("      <PartyName><Name>")
                    .append(escapeXml(nombreCliente))
                    .append("</Name></PartyName>\n");
        }
        xml.append("    </Party>\n");
        xml.append("  </AccountingCustomerParty>\n");

        // Totales
        xml.append("  <LegalMonetaryTotal>\n");
        xml.append("    <TaxExclusiveAmount currencyID=\"PEN\">")
                .append(documento.getSubtotal()).append("</TaxExclusiveAmount>\n");
        xml.append("    <TaxInclusiveAmount currencyID=\"PEN\">")
                .append(documento.getTotal()).append("</TaxInclusiveAmount>\n");
        xml.append("    <PayableAmount currencyID=\"PEN\">")
                .append(documento.getTotal()).append("</PayableAmount>\n");
        xml.append("  </LegalMonetaryTotal>\n");

        // IGV
        xml.append("  <TaxTotal>\n");
        xml.append("    <TaxAmount currencyID=\"PEN\">")
                .append(documento.getImpuestos()).append("</TaxAmount>\n");
        xml.append("    <TaxSubtotal>\n");
        xml.append("      <TaxCategory>\n");
        xml.append("        <TaxScheme><ID>1000</ID><Name>IGV</Name></TaxScheme>\n");
        xml.append("      </TaxCategory>\n");
        xml.append("    </TaxSubtotal>\n");
        xml.append("  </TaxTotal>\n");

        xml.append("</Invoice>");

        return xml.toString();
    }

    private RespuestaPse simularRespuestaSunat(DocumentosFacturacion documento, String xmlGenerado) {
        String hashCdr = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        // Simular aceptación (95% aceptado, 5% rechazado)
        boolean aceptado = Math.random() > 0.05;

        String codigoRespuesta;
        String mensajeRespuesta;
        String observaciones = null;

        if (aceptado) {
            codigoRespuesta = "0";
            mensajeRespuesta = "La " + documento.getTipoDocumento().name()
                    + " " + documento.getNumeroDocumento()
                    + " ha sido aceptada.";

            // 20% probabilidad de observaciones (aceptada con observaciones)
            if (Math.random() < 0.20) {
                codigoRespuesta = "0";
                observaciones = "4252 - El dato ingresado como denominación del receptor no cumple "
                        + "con el estandar - Observación no restrictiva";
                mensajeRespuesta += " [Con observaciones: " + observaciones + "]";
            }
        } else {
            codigoRespuesta = "2800";
            mensajeRespuesta = "El documento " + documento.getNumeroDocumento()
                    + " ha sido rechazado. Motivo: Error en la estructura del documento electrónico "
                    + "(simulación PSE).";
        }

        RespuestaPse respuesta = new RespuestaPse();
        respuesta.setAceptado(aceptado);
        respuesta.setCodigoRespuesta(codigoRespuesta);
        respuesta.setMensajeRespuesta(mensajeRespuesta);
        respuesta.setHashCdr(hashCdr);
        respuesta.setXmlGenerado(xmlGenerado);
        respuesta.setObservaciones(observaciones);
        respuesta.setFechaRespuesta(LocalDateTime.now());

        return respuesta;
    }

    private String escapeXml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    // ═══════════════════════════════════════════════════════════════════
    //  DTO de Respuesta PSE
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Representa la respuesta simulada de SUNAT/PSE.
     */
    public static class RespuestaPse {
        private boolean aceptado;
        private String codigoRespuesta;
        private String mensajeRespuesta;
        private String hashCdr;
        private String xmlGenerado;
        private String observaciones;
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
}
