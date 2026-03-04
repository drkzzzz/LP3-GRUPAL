package DrinkGo.DrinkGo_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.dto.facturacion.RespuestaPse;
import DrinkGo.DrinkGo_backend.entity.DocumentosFacturacion;
import DrinkGo.DrinkGo_backend.entity.HistorialPse;
import DrinkGo.DrinkGo_backend.repository.DocumentosFacturacionRepository;
import DrinkGo.DrinkGo_backend.repository.HistorialPseRepository;

/**
 * Servicio asíncrono para el procesamiento de documentos PSE.
 * <p>
 * Se invoca desde {@link AdminFacturacionController} después de marcar
 * el documento como {@code enviado}. Duerme el tiempo de simulación
 * y luego llama al PSE (simulador) para obtener el resultado final.
 * <p>
 * IMPORTANT: {@code procesarDocumentoAsync} NO usa {@code @Transactional} a
 * nivel de método. Si se usara, un fallo en el save final marcaría la
 * transacción como rollback-only ANTES de que el catch pueda revertir el
 * estado, dejando el doc atascado en {@code enviado} para siempre.
 * En su lugar, cada {@code save()} usa su propia transacción corta
 * (Spring Data JPA lo garantiza por defecto).
 * Las relaciones LAZY se resuelven con {@code findByIdWithRelations}
 * (JOIN FETCH) para evitar LazyInitializationException fuera de sesión.
 */
@Service
public class PseAsyncService {

    /** Tiempo de simulación en milisegundos (3 segundos — suficiente para simular latencia SUNAT). */
    private static final int DELAY_MS = 3_000;

    @Autowired private DocumentosFacturacionRepository documentosRepo;
    @Autowired private HistorialPseRepository historialPseRepo;
    @Autowired private PseProviderFactory pseProviderFactory;

    /**
     * Al arrancar el backend, revierte a {@code pendiente_envio} todos los documentos
     * que quedaron en estado {@code enviado} (p.ej. el JVM fue matado durante el async).
     * Usa {@code @EventListener(ApplicationReadyEvent.class)} para garantizar que el contexto
     * Spring y las transacciones JPA estén completamente listos antes de ejecutar.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void recuperarDocumentosAtascados() {
        List<DocumentosFacturacion> atascados = documentosRepo
                .findByEstadoDocumento(DocumentosFacturacion.EstadoDocumento.enviado);
        if (!atascados.isEmpty()) {
            for (DocumentosFacturacion doc : atascados) {
                doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.pendiente_envio);
                doc.setRespuestaSunat("Reintento requerido: el servidor se reinició mientras procesaba");
                documentosRepo.save(doc);
            }
            System.out.println("[PSE Recovery] " + atascados.size()
                    + " documento(s) revertidos de 'enviado' → 'pendiente_envio'");
        }
    }

    /**
     * Procesa asincrónicamente un documento PSE.
     * <p>
     * NO lleva {@code @Transactional}: cada {@code save()} corre en su propia
     * transacción corta. Esto garantiza que si el save del estado final falla,
     * el catch pueda persistir el retroceso a {@code pendiente_envio} sin que
     * la transacción esté marcada rollback-only.
     *
     * @param documentoId ID del documento a procesar (debe estar en estado {@code enviado})
     */
    @Async
    public void procesarDocumentoAsync(Long documentoId) {
        // Simular latencia de comunicación con SUNAT
        try {
            Thread.sleep(DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        // Cargar el documento con todas las relaciones LAZY resueltas (JOIN FETCH)
        // para evitar LazyInitializationException al acceder a negocio, cliente, serie, etc.
        DocumentosFacturacion doc = documentosRepo.findByIdWithRelations(documentoId).orElse(null);
        if (doc == null) return;

        // Si ya no está en estado "enviado" (p.ej. fue anulado/reseteado), no hacer nada
        if (doc.getEstadoDocumento() != DocumentosFacturacion.EstadoDocumento.enviado) return;

        Long negocioId = doc.getNegocio().getId();

        try {
            // Llamar al proveedor PSE (siempre simulador en este entorno)
            RespuestaPse respuesta = pseProviderFactory.getProvider(negocioId).enviarDocumento(doc);

            doc.setHashSunat(respuesta.getHashCdr());
            doc.setRespuestaSunat(respuesta.getMensajeRespuesta());
            doc.setXmlDocumento(respuesta.getXmlGenerado());
            doc.setFechaEnvio(LocalDateTime.now());
            doc.setCodigoRespuestaSunat(respuesta.getCodigoRespuesta());
            doc.setIntentosEnvio((doc.getIntentosEnvio() != null ? doc.getIntentosEnvio() : 0) + 1);

            if (respuesta.isAceptado()) {
                if (respuesta.getObservaciones() != null && !respuesta.getObservaciones().isBlank()) {
                    doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.observado);
                } else {
                    doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.aceptado);
                }
            } else {
                doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.rechazado);
            }

            // Este save corre en su propia transacción corta (no hay transacción activa)
            doc = documentosRepo.save(doc);

            // Registrar en historial PSE (no crítico — su propio try/catch interno)
            String proveedorNombre = pseProviderFactory.getProveedorActual(negocioId);
            registrarHistorial(doc, "ENVIO", respuesta, proveedorNombre);

        } catch (Exception ex) {
            // Si falla el PSE o el save, revertir a pendiente_envio para reintento.
            // Al NO tener @Transactional en el método, este save corre en su propia
            // transacción independiente y SIEMPRE persiste correctamente.
            System.err.println("[PSE Error] Documento " + documentoId + ": " + ex.getMessage());
            try {
                // Recargar para tener un estado limpio (el doc pudo haber quedado "sucio")
                DocumentosFacturacion docFresh = documentosRepo.findById(documentoId).orElse(null);
                if (docFresh != null) {
                    docFresh.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.pendiente_envio);
                    docFresh.setRespuestaSunat("Error PSE: " + ex.getMessage());
                    documentosRepo.save(docFresh);
                }
            } catch (Exception saveEx) {
                System.err.println("[PSE Error] No se pudo revertir documento " + documentoId
                        + ": " + saveEx.getMessage());
            }
        }
    }

    // ─── Helper: registrar en historial PSE ───────────────────────────
    private void registrarHistorial(DocumentosFacturacion doc, String tipoOperacion,
                                    RespuestaPse respuesta, String proveedor) {
        try {
            HistorialPse historial = new HistorialPse();
            historial.setNegocio(doc.getNegocio());
            historial.setDocumento(doc);
            historial.setTipoOperacion(tipoOperacion);
            historial.setNumeroDocumento(doc.getNumeroDocumento());
            historial.setRequestPayload(respuesta.getXmlGenerado());
            historial.setResponsePayload(respuesta.getMensajeRespuesta());
            historial.setCodigoRespuesta(respuesta.getCodigoRespuesta());
            historial.setMensajeRespuesta(respuesta.getMensajeRespuesta());
            historial.setExitoso(respuesta.isAceptado());
            historial.setIntentoNumero(doc.getIntentosEnvio());
            historialPseRepo.save(historial);
        } catch (Exception ignored) {
            // historial no crítico
        }
    }
}
