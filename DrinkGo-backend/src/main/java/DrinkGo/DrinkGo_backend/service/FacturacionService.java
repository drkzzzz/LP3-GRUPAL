package DrinkGo.DrinkGo_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.repository.*;

/**
 * Servicio de Facturación Electrónica.
 * <p>
 * Gestiona el ciclo de vida de los documentos de facturación:
 * <ul>
 *   <li>Emisión automática de comprobantes desde ventas POS</li>
 *   <li>Numeración automática con series y correlativos</li>
 *   <li>Simulación de envío a SUNAT (PSE)</li>
 *   <li>Anulación de comprobantes</li>
 * </ul>
 * <p>
 * Usa {@code REQUIRES_NEW} para que los fallos de facturación
 * NO reviertan la transacción de venta.
 */
@Service
public class FacturacionService {

    @Autowired private SeriesFacturacionRepository seriesRepo;
    @Autowired private DocumentosFacturacionRepository documentosRepo;
    @Autowired private ClientesRepository clientesRepo;

    @Autowired private PseSimuladorService pseSimulador;

    // ═══════════════════════════════════════════════════════════════════
    //  EMISIÓN DE COMPROBANTE DESDE VENTA
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Emite un comprobante de facturación electrónica a partir de una venta.
     * <p>
     * Se ejecuta en una transacción NUEVA (REQUIRES_NEW) para que un fallo
     * aquí no revierte la venta en PosService.
     * <p>
     * Flujo:
     * 1. Determina tipo de documento (boleta/factura)
     * 2. Busca serie predeterminada
     * 3. Genera número de documento con correlativo
     * 4. Resuelve cliente (o crea "consumidor final" para boletas sin cliente)
     * 5. Crea DocumentoFacturacion con estado pendiente_envio
     * 6. Simula envío a SUNAT vía PSE
     * 7. Actualiza estado según respuesta SUNAT
     *
     * @param venta   La venta ya persistida
     * @param usuario El usuario que realiza la operación
     * @return El documento creado, o null si no hay serie configurada
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public DocumentosFacturacion emitirComprobanteDesdeVenta(Ventas venta, Usuarios usuario) {
        // 1. Determinar tipo de documento
        SeriesFacturacion.TipoDocumento tipoDoc;
        if (venta.getTipoComprobante() == Ventas.TipoComprobante.factura) {
            tipoDoc = SeriesFacturacion.TipoDocumento.factura;
        } else {
            tipoDoc = SeriesFacturacion.TipoDocumento.boleta;
        }

        // 2. Buscar serie predeterminada
        Optional<SeriesFacturacion> serieOpt = seriesRepo
                .findByNegocioIdAndTipoDocumentoAndEsPredeterminada(
                        venta.getNegocio().getId(), tipoDoc, true);

        if (serieOpt.isEmpty()) {
            // No hay serie configurada — no emitir comprobante
            return null;
        }

        // 3. Lock y generar correlativo
        SeriesFacturacion serie = seriesRepo.findByIdForUpdate(serieOpt.get().getId())
                .orElseThrow(() -> new RuntimeException("Serie de facturación no encontrada"));
        int correlativo = serie.getNumeroActual();
        serie.setNumeroActual(correlativo + 1);
        seriesRepo.save(serie);

        String numeroDocumento = serie.getSerie() + "-" + String.format("%08d", correlativo);

        // 4. Resolver cliente
        Clientes cliente = resolverClienteParaComprobante(venta);

        // 5. Crear documento
        DocumentosFacturacion doc = new DocumentosFacturacion();
        doc.setNegocio(venta.getNegocio());
        doc.setSerieFacturacion(serie);
        doc.setTipoDocumento(DocumentosFacturacion.TipoDocumento.valueOf(tipoDoc.name()));
        doc.setNumeroDocumento(numeroDocumento);
        doc.setVenta(venta);
        doc.setCliente(cliente);
        doc.setUsuario(usuario);
        doc.setFechaEmision(LocalDate.now());
        doc.setSubtotal(venta.getSubtotal());
        doc.setImpuestos(venta.getMontoImpuesto());
        doc.setTotal(venta.getTotal());
        doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.pendiente_envio);

        doc = documentosRepo.save(doc);

        // 6. Simular envío a SUNAT (PSE)
        try {
            PseSimuladorService.RespuestaPse respuesta = pseSimulador.enviarDocumento(doc);

            doc.setHashSunat(respuesta.getHashCdr());
            doc.setRespuestaSunat(respuesta.getMensajeRespuesta());
            doc.setXmlDocumento(respuesta.getXmlGenerado());

            if (respuesta.isAceptado()) {
                doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.aceptado);
            } else {
                doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.rechazado);
            }

            documentosRepo.save(doc);
        } catch (Exception ex) {
            // Si falla la simulación PSE, dejar en pendiente_envio
            doc.setRespuestaSunat("Error PSE: " + ex.getMessage());
            documentosRepo.save(doc);
        }

        return doc;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ANULACIÓN DE COMPROBANTES
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Anula todos los comprobantes asociados a una venta.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void anularComprobantesPorVenta(Long ventaId) {
        List<DocumentosFacturacion> docs = documentosRepo.findByVentaId(ventaId);
        for (DocumentosFacturacion doc : docs) {
            if (doc.getEstadoDocumento() != DocumentosFacturacion.EstadoDocumento.anulado) {
                doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.anulado);
                doc.setRespuestaSunat("Anulado por anulación de venta");
                documentosRepo.save(doc);
            }
        }
    }

    /**
     * Reenvía un comprobante pendiente a SUNAT.
     */
    @Transactional
    public DocumentosFacturacion reenviarComprobante(Long documentoId) {
        DocumentosFacturacion doc = documentosRepo.findById(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));

        if (doc.getEstadoDocumento() != DocumentosFacturacion.EstadoDocumento.pendiente_envio
                && doc.getEstadoDocumento() != DocumentosFacturacion.EstadoDocumento.rechazado) {
            throw new IllegalStateException("Solo se pueden reenviar documentos pendientes o rechazados");
        }

        PseSimuladorService.RespuestaPse respuesta = pseSimulador.enviarDocumento(doc);

        doc.setHashSunat(respuesta.getHashCdr());
        doc.setRespuestaSunat(respuesta.getMensajeRespuesta());
        doc.setXmlDocumento(respuesta.getXmlGenerado());
        doc.setEstadoDocumento(respuesta.isAceptado()
                ? DocumentosFacturacion.EstadoDocumento.aceptado
                : DocumentosFacturacion.EstadoDocumento.rechazado);

        return documentosRepo.save(doc);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Resuelve el cliente para el comprobante.
     * <p>
     * - Si la venta tiene cliente asignado, usa ese.
     * - Si es boleta sin cliente, busca o crea un "Consumidor Final" genérico.
     * - Si es factura sin cliente, lanza excepción (factura requiere RUC).
     */
    private Clientes resolverClienteParaComprobante(Ventas venta) {
        // Si la venta tiene cliente, usarlo
        if (venta.getCliente() != null) {
            return venta.getCliente();
        }

        // Para facturas, el cliente es obligatorio
        if (venta.getTipoComprobante() == Ventas.TipoComprobante.factura) {
            throw new IllegalStateException("La factura requiere un cliente con RUC");
        }

        // Para boletas sin cliente → "Consumidor Final"
        Long negocioId = venta.getNegocio().getId();
        Optional<Clientes> genericoOpt = clientesRepo
                .findByNegocioIdAndNumeroDocumento(negocioId, "00000000");

        if (genericoOpt.isPresent()) {
            return genericoOpt.get();
        }

        // Crear cliente genérico "Consumidor Final"
        Clientes consumidorFinal = new Clientes();
        consumidorFinal.setNegocio(venta.getNegocio());
        consumidorFinal.setTipoDocumento(Clientes.TipoDocumento.DNI);
        consumidorFinal.setNumeroDocumento("00000000");
        consumidorFinal.setNombres("Consumidor");
        consumidorFinal.setApellidos("Final");
        return clientesRepo.save(consumidorFinal);
    }
}
