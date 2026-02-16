package DrinkGo.DrinkGo_backend.integration.pse;

import DrinkGo.DrinkGo_backend.entity.DocumentoFacturacion;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

/**
 * Implementación SIMULADA del cliente PSE para desarrollo y pruebas.
 * 
 * COMPORTAMIENTO SIMULADO:
 * - 80% de probabilidad: documento ACEPTADO por SUNAT
 * - 15% de probabilidad: documento OBSERVADO (aceptado con observaciones)
 * - 5% de probabilidad: documento RECHAZADO
 * 
 * NOTA IMPORTANTE:
 * Esta implementación NO conecta realmente con SUNAT ni con ningún PSE.
 * Genera respuestas simuladas para permitir el desarrollo y testing completo
 * del flujo de facturación electrónica.
 * 
 * Para integración real, crear una nueva clase que implemente PseClient
 * (ej: PseNubefactClient) y registrarla como @Primary o usar @Profile.
 */
@Component
public class PseSimulationClient implements PseClient {

    private static final Random random = new Random();

    @Override
    public PseResponse enviarDocumento(DocumentoFacturacion documento) {
        // Simular tiempo de procesamiento
        simularLatencia();

        // Generar hash simulado del documento
        String hashSimulado = generarHashSimulado(documento);

        // Generar ticket simulado
        String ticketSimulado = generarTicketSimulado();

        // Generar URLs simuladas
        String baseUrl = "https://drinkgo-pse-simulacion.local/documentos/";
        String urlXml = baseUrl + documento.getNumeroCompleto() + ".xml";
        String urlCdr = baseUrl + documento.getNumeroCompleto() + "-cdr.xml";

        // Determinar resultado: 80% aceptado, 15% observado, 5% rechazado
        int probabilidad = random.nextInt(100);

        // Boletas se envían vía Resumen Diario, facturas individualmente
        boolean esBoleta = documento.getTipoDocumento()
                == DocumentoFacturacion.TipoDocumento.boleta;
        String mecanismo = esBoleta ? "Resumen Diario" : "envío individual";

        if (probabilidad < 80) {
            // ACEPTADO (80%)
            return new PseResponse(
                    true,
                    "aceptado",
                    "0",
                    "La " + documento.getTipoDocumento().name() + " " + documento.getNumeroCompleto()
                            + " ha sido aceptada por SUNAT vía " + mecanismo + " [SIMULACIÓN]",
                    hashSimulado,
                    urlXml,
                    urlCdr,
                    ticketSimulado
            );
        } else if (probabilidad < 95) {
            // OBSERVADO (15%) - Se acepta pero con observaciones
            String[] observaciones = {
                    "Observación 4252: El nombre del receptor no coincide con datos SUNAT [SIMULACIÓN]",
                    "Observación 4256: Dirección del emisor no registrada en SUNAT [SIMULACIÓN]",
                    "Observación 4260: Tipo de cambio fuera del rango permitido [SIMULACIÓN]"
            };
            String observacion = observaciones[random.nextInt(observaciones.length)];

            return new PseResponse(
                    true,
                    "aceptado",
                    "4252",
                    observacion,
                    hashSimulado,
                    urlXml,
                    urlCdr,
                    ticketSimulado
            );
        } else {
            // RECHAZADO (5%)
            String[] rechazos = {
                    "Error 2033: RUC del emisor no se encuentra activo [SIMULACIÓN]",
                    "Error 2017: El documento ya fue informado previamente [SIMULACIÓN]",
                    "Error 3105: Total no coincide con suma de items [SIMULACIÓN]"
            };
            String rechazo = rechazos[random.nextInt(rechazos.length)];

            return new PseResponse(
                    false,
                    "rechazado",
                    "2033",
                    rechazo,
                    null,
                    null,
                    null,
                    ticketSimulado
            );
        }
    }

    @Override
    public PseResponse consultarEstado(String ticketSunat) {
        simularLatencia();

        return new PseResponse(
                true,
                "aceptado",
                "0",
                "Documento procesado correctamente [SIMULACIÓN - Consulta por ticket]",
                null,
                null,
                null,
                ticketSunat
        );
    }

    @Override
    public PseResponse anularDocumento(DocumentoFacturacion documento, String motivo) {
        simularLatencia();

        // Facturas: Comunicación de Baja. Boletas: Resumen Diario de Baja.
        boolean esBoleta = documento.getTipoDocumento()
                == DocumentoFacturacion.TipoDocumento.boleta;
        String mecanismoAnulacion = esBoleta
                ? "Resumen Diario de Baja" : "Comunicación de Baja";

        String ticketAnulacion = "ANU-" + generarTicketSimulado();

        // Simular: 90% aceptada la anulación, 10% rechazada
        if (random.nextInt(100) < 90) {
            return new PseResponse(
                    true,
                    "anulado",
                    "0",
                    mecanismoAnulacion + " procesada correctamente para "
                            + documento.getNumeroCompleto() + " [SIMULACIÓN]",
                    null,
                    null,
                    null,
                    ticketAnulacion
            );
        } else {
            return new PseResponse(
                    false,
                    "error",
                    "2033",
                    "Error al procesar " + mecanismoAnulacion
                            + " [SIMULACIÓN]",
                    null,
                    null,
                    null,
                    ticketAnulacion
            );
        }
    }

    @Override
    public String getNombreProveedor() {
        return "PSE Simulación DrinkGo (Desarrollo)";
    }

    // --- Métodos auxiliares privados ---

    /**
     * Simula latencia de red (50-200ms).
     */
    private void simularLatencia() {
        try {
            Thread.sleep(50 + random.nextInt(150));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Genera un hash SHA-256 simulado basado en datos del documento.
     */
    private String generarHashSimulado(DocumentoFacturacion documento) {
        try {
            String datos = documento.getNumeroCompleto() + "|"
                    + documento.getRucEmisor() + "|"
                    + documento.getTotal() + "|"
                    + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(datos.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    /**
     * Genera un ticket de SUNAT simulado.
     */
    private String generarTicketSimulado() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "SIM-" + timestamp + "-" + (1000 + random.nextInt(9000));
    }
}
