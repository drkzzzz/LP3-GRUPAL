package DrinkGo.DrinkGo_backend.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;

import DrinkGo.DrinkGo_backend.dto.facturacion.RespuestaPse;
import DrinkGo.DrinkGo_backend.entity.DocumentosFacturacion;

/**
 * Simulador del Proveedor de Servicios Electrónicos (PSE) de SUNAT.
 * <p>
 * Actúa exactamente como lo haría un PSE real (Nubefact, Efact, Digiflow).
 * En producción solo se reemplaza esta clase por la integración real
 * y todo el flujo (frontend, estados, BD) queda intacto.
 * <p>
 * Simula el ciclo completo:
 * <ol>
 *   <li>Validación estructural del comprobante</li>
 *   <li>Generación de XML UBL 2.1</li>
 *   <li>Firma digital simulada (SHA-256)</li>
 *   <li>Envío a "SUNAT" con lógica determinista de respuesta</li>
 *   <li>Generación de CDR (Constancia de Recepción) simulado</li>
 * </ol>
 * <p>
 * Lógica de respuesta determinista:
 * <ul>
 *   <li>RUC receptor = "00000000000" → RECHAZADO (código 2010)</li>
 *   <li>Serie inválida (no empieza con B ni F) → RECHAZADO (código 2022)</li>
 *   <li>Total no coincide con subtotal + IGV → RECHAZADO (código 2047)</li>
 *   <li>Factura con doc receptor &lt; 11 dígitos → RECHAZADO (código 2050)</li>
 *   <li>Total &gt; 7000 → ACEPTADO con observación (código 4252)</li>
 *   <li>Total &gt; 3500 → ACEPTADO con observación (código 4002)</li>
 *   <li>Resto → ACEPTADO (código 0)</li>
 * </ul>
 */
@Service
public class PseSimuladorService implements PseProvider {

    /**
     * Simula el envío de un documento de facturación a SUNAT.
     *
     * @param documento El documento a "enviar"
     * @return RespuestaPse con hash, XML firmado, CDR y código SUNAT
     */
    @Override
    public RespuestaPse enviarDocumento(DocumentosFacturacion documento) {
        // 1. Validación estructural (igual que PSE real)
        validarEstructura(documento);

        // 2. Generar XML UBL 2.1
        String xmlSinFirma = generarXmlUbl(documento);

        // 3. Firmar XML (simulado con SHA-256)
        String firmaDigital = firmarXml(xmlSinFirma);
        String xmlFirmado = inyectarFirmaEnXml(xmlSinFirma, firmaDigital);

        // 4. Simular envío a SUNAT y obtener respuesta
        RespuestaPse respuesta = simularRespuestaSunat(documento, xmlFirmado, firmaDigital);

        return respuesta;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PASO 1: VALIDACIÓN ESTRUCTURAL
    // ═══════════════════════════════════════════════════════════════════

    private void validarEstructura(DocumentosFacturacion documento) {
        if (documento.getNumeroDocumento() == null || documento.getNumeroDocumento().isBlank()) {
            throw new IllegalArgumentException("PSE: Número de documento es requerido");
        }
        if (documento.getTotal() == null || documento.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("PSE: Total del documento debe ser mayor a 0");
        }
        if (documento.getCliente() == null) {
            throw new IllegalArgumentException("PSE: Cliente es requerido para emitir comprobante electrónico");
        }
        if (documento.getTipoDocumento() == DocumentosFacturacion.TipoDocumento.factura) {
            String numDoc = documento.getCliente().getNumeroDocumento();
            if (numDoc == null || numDoc.length() != 11) {
                throw new IllegalArgumentException(
                        "PSE: Factura requiere cliente con RUC de 11 dígitos. Recibido: "
                                + (numDoc != null ? numDoc : "null"));
            }
        }
        if (documento.getSubtotal() == null) {
            throw new IllegalArgumentException("PSE: Subtotal es requerido");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PASO 2: GENERACIÓN DE XML UBL 2.1
    // ═══════════════════════════════════════════════════════════════════

    private String generarXmlUbl(DocumentosFacturacion documento) {
        String tipoDoc = documento.getTipoDocumento() == DocumentosFacturacion.TipoDocumento.boleta
                ? "03" : "01";
        String fechaEmision = documento.getFechaEmision() != null
                ? documento.getFechaEmision().format(DateTimeFormatter.ISO_LOCAL_DATE)
                : LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Invoice xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"\n");
        xml.append("         xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\"\n");
        xml.append("         xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"\n");
        xml.append("         xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n");
        xml.append("  <cbc:UBLVersionID>2.1</cbc:UBLVersionID>\n");
        xml.append("  <cbc:CustomizationID>2.0</cbc:CustomizationID>\n");
        xml.append("  <cbc:ID>").append(documento.getNumeroDocumento()).append("</cbc:ID>\n");
        xml.append("  <cbc:IssueDate>").append(fechaEmision).append("</cbc:IssueDate>\n");
        xml.append("  <cbc:InvoiceTypeCode listID=\"0101\">").append(tipoDoc).append("</cbc:InvoiceTypeCode>\n");
        xml.append("  <cbc:DocumentCurrencyCode>PEN</cbc:DocumentCurrencyCode>\n");

        // Emisor
        xml.append("  <cac:AccountingSupplierParty>\n");
        xml.append("    <cac:Party>\n");
        if (documento.getNegocio() != null) {
            xml.append("      <cac:PartyIdentification>\n");
            xml.append("        <cbc:ID schemeID=\"6\">")
                    .append(documento.getNegocio().getRuc() != null ? documento.getNegocio().getRuc() : "00000000000")
                    .append("</cbc:ID>\n");
            xml.append("      </cac:PartyIdentification>\n");
            xml.append("      <cac:PartyName><cbc:Name>")
                    .append(escapeXml(documento.getNegocio().getRazonSocial()))
                    .append("</cbc:Name></cac:PartyName>\n");
        }
        xml.append("    </cac:Party>\n");
        xml.append("  </cac:AccountingSupplierParty>\n");

        // Receptor
        xml.append("  <cac:AccountingCustomerParty>\n");
        xml.append("    <cac:Party>\n");
        if (documento.getCliente() != null) {
            String tipoDocCliente = documento.getTipoDocumento() == DocumentosFacturacion.TipoDocumento.factura
                    ? "6" : "1"; // 6=RUC, 1=DNI
            xml.append("      <cac:PartyIdentification>\n");
            xml.append("        <cbc:ID schemeID=\"").append(tipoDocCliente).append("\">")
                    .append(documento.getCliente().getNumeroDocumento())
                    .append("</cbc:ID>\n");
            xml.append("      </cac:PartyIdentification>\n");
            String nombreCliente = documento.getCliente().getRazonSocial() != null
                    ? documento.getCliente().getRazonSocial()
                    : (documento.getCliente().getNombres() + " " + documento.getCliente().getApellidos());
            xml.append("      <cac:PartyName><cbc:Name>")
                    .append(escapeXml(nombreCliente))
                    .append("</cbc:Name></cac:PartyName>\n");
        }
        xml.append("    </cac:Party>\n");
        xml.append("  </cac:AccountingCustomerParty>\n");

        // IGV
        xml.append("  <cac:TaxTotal>\n");
        xml.append("    <cbc:TaxAmount currencyID=\"PEN\">")
                .append(documento.getImpuestos()).append("</cbc:TaxAmount>\n");
        xml.append("    <cac:TaxSubtotal>\n");
        xml.append("      <cbc:TaxableAmount currencyID=\"PEN\">")
                .append(documento.getSubtotal()).append("</cbc:TaxableAmount>\n");
        xml.append("      <cbc:TaxAmount currencyID=\"PEN\">")
                .append(documento.getImpuestos()).append("</cbc:TaxAmount>\n");
        xml.append("      <cac:TaxCategory>\n");
        xml.append("        <cac:TaxScheme><cbc:ID>1000</cbc:ID><cbc:Name>IGV</cbc:Name></cac:TaxScheme>\n");
        xml.append("      </cac:TaxCategory>\n");
        xml.append("    </cac:TaxSubtotal>\n");
        xml.append("  </cac:TaxTotal>\n");

        // Totales
        xml.append("  <cac:LegalMonetaryTotal>\n");
        xml.append("    <cbc:TaxExclusiveAmount currencyID=\"PEN\">")
                .append(documento.getSubtotal()).append("</cbc:TaxExclusiveAmount>\n");
        xml.append("    <cbc:TaxInclusiveAmount currencyID=\"PEN\">")
                .append(documento.getTotal()).append("</cbc:TaxInclusiveAmount>\n");
        xml.append("    <cbc:PayableAmount currencyID=\"PEN\">")
                .append(documento.getTotal()).append("</cbc:PayableAmount>\n");
        xml.append("  </cac:LegalMonetaryTotal>\n");

        // Placeholder para firma (se inyecta después)
        xml.append("  <!-- SIGNATURE_PLACEHOLDER -->\n");
        xml.append("</Invoice>");

        return xml.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PASO 3: FIRMA DIGITAL SIMULADA
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Genera una firma digital simulada usando SHA-256 del XML.
     * En producción esto sería una firma real con certificado digital.
     */
    private String firmarXml(String xml) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(xml.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "SIMULATED_SIGNATURE_" + UUID.randomUUID().toString().replace("-", "");
        }
    }

    /**
     * Inyecta el nodo de firma en el XML (simulando firma XAdES-BES).
     */
    private String inyectarFirmaEnXml(String xml, String firma) {
        String signatureNode =
                "  <ds:Signature Id=\"SignatureDrinkGo\">\n" +
                "    <ds:SignedInfo>\n" +
                "      <ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n" +
                "      <ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>\n" +
                "    </ds:SignedInfo>\n" +
                "    <ds:SignatureValue>" + firma + "</ds:SignatureValue>\n" +
                "    <ds:KeyInfo>\n" +
                "      <ds:X509Data>\n" +
                "        <ds:X509Certificate>SIMULATED_CERTIFICATE</ds:X509Certificate>\n" +
                "      </ds:X509Data>\n" +
                "    </ds:KeyInfo>\n" +
                "  </ds:Signature>";
        return xml.replace("  <!-- SIGNATURE_PLACEHOLDER -->", signatureNode);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PASO 4: SIMULACIÓN DE RESPUESTA SUNAT + CDR
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Simula la respuesta de SUNAT con lógica determinista:
     * <ul>
     *   <li>RUC "00000000000" → RECHAZADO (2010 - RUC receptor no válido)</li>
     *   <li>Boleta con DNI "00000000" (DNI ficticio) → RECHAZADO (2034 - DNI receptor no existe en RENIEC)</li>
     *   <li>Serie no válida (no empieza con B ni F) → RECHAZADO (2022 - Serie no corresponde)</li>
     *   <li>Subtotal + impuestos != total (diferencia > 1) → RECHAZADO (2047 - Total no coincide)</li>
     *   <li>Tipo doc receptor inválido (factura con DNI corto) → RECHAZADO (2050 - Tipo doc receptor no válido)</li>
     *   <li>Total > 7000 → ACEPTADO con observación (4252 - bancarización)</li>
     *   <li>Total entre 3500 y 7000 → ACEPTADO con observación (4002 - advertencia montos)</li>
     *   <li>Resto → ACEPTADO (0)</li>
     * </ul>
     */
    private RespuestaPse simularRespuestaSunat(DocumentosFacturacion documento,
                                                String xmlFirmado, String firmaDigital) {
        String numDoc = documento.getNumeroDocumento();
        String rucReceptor = documento.getCliente() != null
                ? documento.getCliente().getNumeroDocumento() : null;
        BigDecimal total = documento.getTotal();
        BigDecimal subtotal = documento.getSubtotal();
        BigDecimal impuestos = documento.getImpuestos();

        boolean aceptado;
        String codigoRespuesta;
        String mensajeRespuesta;
        String observaciones = null;

        // ─── Reglas deterministas de SUNAT simulada ───

        // Regla 1: RUC receptor inválido "00000000000" → rechazo (código 2010)
        if ("00000000000".equals(rucReceptor)) {
            aceptado = false;
            codigoRespuesta = "2010";
            mensajeRespuesta = "El documento " + numDoc
                    + " ha sido rechazado. Motivo: El RUC del receptor no es válido "
                    + "(RUC 00000000000 no existe en el padrón de contribuyentes).";
        }
        // Regla 1b: Boleta con DNI "00000000" (DNI ficticio/vacío) → rechazo (código 2034)
        else if (documento.getTipoDocumento() == DocumentosFacturacion.TipoDocumento.boleta
                && "00000000".equals(rucReceptor)) {
            aceptado = false;
            codigoRespuesta = "2034";
            mensajeRespuesta = "El documento " + numDoc
                    + " ha sido rechazado. Motivo: El número de DNI del receptor no es válido "
                    + "(00000000 no existe en el padrón de RENIEC)."; 
        }
        // Regla 2: Serie no corresponde al tipo de comprobante (código 2022)
        else if (numDoc != null && !numDoc.startsWith("B") && !numDoc.startsWith("F")
                && !numDoc.startsWith("b") && !numDoc.startsWith("f")) {
            aceptado = false;
            codigoRespuesta = "2022";
            mensajeRespuesta = "El documento " + numDoc
                    + " ha sido rechazado. Motivo: La serie del comprobante no corresponde al tipo de documento.";
        }
        // Regla 3: Total no coincide con subtotal + impuestos (código 2047)
        else if (subtotal != null && impuestos != null && total != null
                && subtotal.add(impuestos).subtract(total).abs().compareTo(new BigDecimal("1")) > 0) {
            aceptado = false;
            codigoRespuesta = "2047";
            mensajeRespuesta = "El documento " + numDoc
                    + " ha sido rechazado. Motivo: El total del comprobante no coincide con la suma de los ítems "
                    + "(Subtotal: " + subtotal + " + IGV: " + impuestos + " ≠ Total: " + total + ").";
        }
        // Regla 4: Factura con documento receptor menor a 11 dígitos (código 2050)
        else if (documento.getTipoDocumento() == DocumentosFacturacion.TipoDocumento.factura
                && rucReceptor != null && rucReceptor.length() < 11) {
            aceptado = false;
            codigoRespuesta = "2050";
            mensajeRespuesta = "El documento " + numDoc
                    + " ha sido rechazado. Motivo: El tipo de documento de identidad del receptor no es válido "
                    + "para facturas (se requiere RUC de 11 dígitos).";
        }
        // Regla 5: Total > 7000 → aceptado con observación (código 4252 - bancarización)
        else if (total != null && total.compareTo(new BigDecimal("7000")) > 0) {
            aceptado = true;
            codigoRespuesta = "0";
            observaciones = "4252 - El monto total del comprobante supera el límite para operaciones "
                    + "sin bancarización (S/ 7,000.00). Verificar medio de pago.";
            mensajeRespuesta = "La " + documento.getTipoDocumento().name()
                    + " " + numDoc + " ha sido aceptada. [Con observaciones: " + observaciones + "]";
        }
        // Regla 6: Total entre 3500 y 7000 → aceptado con advertencia (código 4002)
        else if (total != null && total.compareTo(new BigDecimal("3500")) > 0) {
            aceptado = true;
            codigoRespuesta = "0";
            observaciones = "4002 - Advertencia: El monto del comprobante es considerable (S/ "
                    + total.toPlainString() + "). Se recomienda verificar datos del receptor.";
            mensajeRespuesta = "La " + documento.getTipoDocumento().name()
                    + " " + numDoc + " ha sido aceptada. [Con observaciones: " + observaciones + "]";
        }
        // Regla 7: Todo OK → aceptado (código 0)
        else {
            aceptado = true;
            codigoRespuesta = "0";
            mensajeRespuesta = "La " + documento.getTipoDocumento().name()
                    + " " + numDoc + " ha sido aceptada.";
        }

        // Generar hash CDR
        String hashCdr = firmaDigital.substring(0, Math.min(40, firmaDigital.length()));

        // Generar CDR simulado en Base64
        String cdrBase64 = generarCdrBase64(documento, codigoRespuesta, mensajeRespuesta);

        RespuestaPse respuesta = new RespuestaPse();
        respuesta.setAceptado(aceptado);
        respuesta.setCodigoRespuesta(codigoRespuesta);
        respuesta.setMensajeRespuesta(mensajeRespuesta);
        respuesta.setHashCdr(hashCdr);
        respuesta.setXmlGenerado(xmlFirmado);
        respuesta.setObservaciones(observaciones);
        respuesta.setFirmaDigital(firmaDigital);
        respuesta.setCdrBase64(cdrBase64);
        respuesta.setFechaRespuesta(LocalDateTime.now());

        return respuesta;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PASO 5: GENERACIÓN DE CDR SIMULADO
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Genera un CDR (Constancia de Recepción) simulado en Base64.
     * En producción, SUNAT devuelve esto como un ZIP con XML dentro.
     */
    private String generarCdrBase64(DocumentosFacturacion documento,
                                     String codigoRespuesta, String mensaje) {
        String cdrXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<ApplicationResponse xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2\">\n"
                + "  <ID>" + UUID.randomUUID().toString() + "</ID>\n"
                + "  <IssueDate>" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "</IssueDate>\n"
                + "  <ResponseCode>" + codigoRespuesta + "</ResponseCode>\n"
                + "  <Note>" + escapeXml(mensaje) + "</Note>\n"
                + "  <DocumentReference>" + documento.getNumeroDocumento() + "</DocumentReference>\n"
                + "</ApplicationResponse>";

        return Base64.getEncoder().encodeToString(cdrXml.getBytes(StandardCharsets.UTF_8));
    }

    // ═══════════════════════════════════════════════════════════════════
    //  UTILS
    // ═══════════════════════════════════════════════════════════════════

    private String escapeXml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
