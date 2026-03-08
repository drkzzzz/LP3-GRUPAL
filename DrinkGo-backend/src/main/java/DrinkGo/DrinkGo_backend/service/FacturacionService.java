package DrinkGo.DrinkGo_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.dto.facturacion.CrearNotaCreditoRequest;
import DrinkGo.DrinkGo_backend.dto.facturacion.RespuestaPse;
import DrinkGo.DrinkGo_backend.entity.HistorialPse;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.repository.*;

/**
 * Servicio de Facturación Electrónica.
 * <p>
 * Gestiona el ciclo de vida de los documentos de facturación:
 * <ul>
 *   <li>Emisión automática de comprobantes desde ventas POS</li>
 *   <li>Numeración automática con series y correlativos</li>
 *   <li>Envío a SUNAT vía PSE (proveedor configurable)</li>
 *   <li>Registro de historial de comunicaciones PSE</li>
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
    @Autowired private DetalleDocumentosFacturacionRepository detalleDocRepo;
    @Autowired private DetalleVentasRepository detalleVentasRepo;
    @Autowired private ClientesRepository clientesRepo;
    @Autowired private StockInventarioRepository stockRepo;
    @Autowired private MovimientosInventarioRepository movInventarioRepo;
    @Autowired private AlmacenesRepository almacenesRepo;
    @Autowired private VentasRepository ventasRepo;
    @Autowired private PagosVentaRepository pagosVentaRepo;
    @Autowired private MovimientosCajaRepository movimientosRepo;
    @Autowired private UsuariosRepository usuariosRepo;

    @Autowired private PseProviderFactory pseProviderFactory;
    @Autowired private HistorialPseRepository historialPseRepo;
    @Autowired private ConfiguracionPseRepository configPseRepo;

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
    public DocumentosFacturacion emitirComprobanteDesdeVenta(Ventas venta, Usuarios usuario) {
        // Nota de venta es un documento interno — no genera comprobante SUNAT
        if (venta.getTipoComprobante() == Ventas.TipoComprobante.nota_venta) {
            return null;
        }

        // 1. Determinar tipo de documento
        SeriesFacturacion.TipoDocumento tipoDoc;
        if (venta.getTipoComprobante() == Ventas.TipoComprobante.factura) {
            tipoDoc = SeriesFacturacion.TipoDocumento.factura;
        } else {
            tipoDoc = SeriesFacturacion.TipoDocumento.boleta;
        }

        // 2. Buscar serie predeterminada
        Optional<SeriesFacturacion> serieOpt = seriesRepo
                .findFirstByNegocioIdAndTipoDocumentoAndEsPredeterminada(
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

        // Determinar modo de emisión según configuración del negocio
        boolean usarPse = Boolean.TRUE.equals(venta.getNegocio().getTienePse());
        doc.setModoEmision(usarPse
                ? DocumentosFacturacion.ModoEmision.PSE
                : DocumentosFacturacion.ModoEmision.LOCAL);

        doc = documentosRepo.save(doc);

        if (!usarPse) {
            // Sin PSE: comprobante interno aceptado directamente
            doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.aceptado);
            doc.setRespuestaSunat("Emitido localmente (sin PSE)");
            documentosRepo.save(doc);
        }
        // Con PSE: el documento queda en pendiente_envio.
        // El usuario lo enviará manualmente desde la bandeja electrónica.

        return doc;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  EMISIÓN DE NOTA DE CRÉDITO / DÉBITO
    // ═══════════════════════════════════════════════════════════════════

    /** Motivos NC que representan una anulación completa (copiar todos los ítems). */
    private static final Set<String> MOTIVOS_ANULACION_COMPLETA = Set.of("01", "02", "03", "06");

    /** Motivo NC: devolución parcial por ítem. */
    private static final String MOTIVO_DEVOLUCION_ITEM = "07";

    /** Motivos NC: descuento/disminución (requieren monto). */
    private static final Set<String> MOTIVOS_DESCUENTO = Set.of("04", "09");

    /**
     * Emite una Nota de Crédito o Nota de Débito referenciando un comprobante original.
     * <p>
     * Comportamiento según motivo:
     * <ul>
     *   <li><b>NC 01/02/03/06</b>: Anulación completa. Copia todos los ítems del original.
     *       Marca el comprobante original como "anulado" vía NC.</li>
     *   <li><b>NC 07</b>: Devolución parcial. Requiere {@code items} con cantidades a devolver.
     *       Devuelve stock al almacén predeterminado.</li>
     *   <li><b>NC 04/09</b>: Descuento/disminución. Requiere {@code monto} de ajuste.</li>
     *   <li><b>ND 01/02/03</b>: Cargo adicional. Requiere {@code monto}.</li>
     * </ul>
     * <p>
     * Validaciones:
     * <ul>
     *   <li>No se permite crear notas sobre otras notas.</li>
     *   <li>No se permite crear notas sobre documentos anulados.</li>
     *   <li>La suma de NC existentes + nueva no puede superar el total original.</li>
     * </ul>
     */
    @Transactional
    public DocumentosFacturacion emitirNotaCreditoDebito(CrearNotaCreditoRequest request) {

        // ── 1. Validar documento de referencia ──
        DocumentosFacturacion docOriginal = documentosRepo.findById(request.getDocumentoReferenciaId())
                .orElseThrow(() -> new IllegalArgumentException("Documento de referencia no encontrado"));

        if (docOriginal.getTipoDocumento() != DocumentosFacturacion.TipoDocumento.boleta
                && docOriginal.getTipoDocumento() != DocumentosFacturacion.TipoDocumento.factura) {
            throw new IllegalStateException(
                    "Solo se puede emitir NC/ND sobre boletas o facturas, no sobre " + docOriginal.getTipoDocumento());
        }

        if (docOriginal.getEstadoDocumento() == DocumentosFacturacion.EstadoDocumento.anulado) {
            throw new IllegalStateException("No se puede emitir NC/ND sobre un documento anulado");
        }

        // ── 2. Determinar tipo de documento NC o ND ──
        String tipoNota = request.getTipoNota();
        DocumentosFacturacion.TipoDocumento tipoDoc;
        SeriesFacturacion.TipoDocumento tipoDocSerie;
        boolean esNotaCredito;
        if ("nota_credito".equals(tipoNota)) {
            tipoDoc = DocumentosFacturacion.TipoDocumento.nota_credito;
            tipoDocSerie = SeriesFacturacion.TipoDocumento.nota_credito;
            esNotaCredito = true;
        } else if ("nota_debito".equals(tipoNota)) {
            tipoDoc = DocumentosFacturacion.TipoDocumento.nota_debito;
            tipoDocSerie = SeriesFacturacion.TipoDocumento.nota_debito;
            esNotaCredito = false;
        } else {
            throw new IllegalArgumentException("tipoNota debe ser 'nota_credito' o 'nota_debito'");
        }

        String codigoMotivo = request.getCodigoMotivo();

        // ── 3. Validar acumulado de NC vs total original ──
        if (esNotaCredito) {
            BigDecimal totalNcExistentes = documentosRepo
                    .sumTotalNotasCreditoByDocumentoReferenciaId(docOriginal.getId());
            BigDecimal totalOriginal = docOriginal.getTotal();

            if (totalNcExistentes.compareTo(totalOriginal) >= 0) {
                throw new IllegalStateException(
                        "El comprobante ya está completamente cubierto por notas de crédito anteriores (S/ "
                        + totalNcExistentes.setScale(2, RoundingMode.HALF_UP)
                        + " de S/ " + totalOriginal.setScale(2, RoundingMode.HALF_UP) + ")");
            }
        }

        // ── 4. Buscar serie predeterminada con prefijo correcto ──
        Long negocioId = docOriginal.getNegocio().getId();
        boolean docOriginalEsBoleta = docOriginal.getTipoDocumento() == DocumentosFacturacion.TipoDocumento.boleta;
        String expectedPrefix;
        String tipoOrigen = docOriginalEsBoleta ? "Boleta" : "Factura";
        String tipoNombreNota = esNotaCredito ? "Nota de Crédito" : "Nota de Débito";
        if (esNotaCredito) {
            expectedPrefix = docOriginalEsBoleta ? "BC" : "FC";
        } else {
            expectedPrefix = docOriginalEsBoleta ? "BD" : "FD";
        }

        Optional<SeriesFacturacion> serieOpt = seriesRepo
                .findDefaultByNegocioIdAndTipoAndPrefix(negocioId, tipoDocSerie.name(), expectedPrefix);
        if (serieOpt.isEmpty()) {
            throw new IllegalStateException(
                    "No hay serie predeterminada con prefijo '" + expectedPrefix + "' configurada para "
                    + tipoNombreNota + " sobre " + tipoOrigen
                    + ". Vaya a Facturación → Series y cree una serie que empiece con '"
                    + expectedPrefix + "' (ej: " + expectedPrefix + "01) marcándola como predeterminada.");
        }

        // ── 5. Lock y generar correlativo ──
        SeriesFacturacion serie = seriesRepo.findByIdForUpdate(serieOpt.get().getId())
                .orElseThrow(() -> new RuntimeException("Serie de facturación no encontrada"));
        int correlativo = serie.getNumeroActual();
        serie.setNumeroActual(correlativo + 1);
        seriesRepo.save(serie);
        String numeroDocumento = serie.getSerie() + "-" + String.format("%08d", correlativo);

        // ── 6. Resolver usuario ──
        Usuarios usuario = null;
        if (request.getUsuarioId() != null) {
            usuario = new Usuarios();
            usuario.setId(request.getUsuarioId());
        }

        // ── 7. Calcular montos e ítems según modalidad ──
        BigDecimal subtotal;
        BigDecimal impuestos;
        BigDecimal total;
        List<DetalleDocumentosFacturacion> detallesNota = new ArrayList<>();

        // Obtener tasa de IGV del negocio (default 18%)
        BigDecimal tasaIgv = BigDecimal.valueOf(18);
        try {
            if (docOriginal.getNegocio().getPorcentajeIgv() != null) {
                tasaIgv = docOriginal.getNegocio().getPorcentajeIgv();
            }
        } catch (Exception ignored) { /* usar default 18% */ }
        BigDecimal factorIgv = BigDecimal.ONE.add(tasaIgv.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));

        if (esNotaCredito && MOTIVOS_ANULACION_COMPLETA.contains(codigoMotivo)) {
            // ── ANULACIÓN COMPLETA: copiar montos del documento original ──
            subtotal = docOriginal.getSubtotal();
            impuestos = docOriginal.getImpuestos();
            total = docOriginal.getTotal();

            // Copiar ítems del documento original (si existen)
            List<DetalleDocumentosFacturacion> detallesOriginales =
                    detalleDocRepo.findByDocumentoFacturacionId(docOriginal.getId());

            if (!detallesOriginales.isEmpty()) {
                for (DetalleDocumentosFacturacion detOrig : detallesOriginales) {
                    DetalleDocumentosFacturacion detNota = new DetalleDocumentosFacturacion();
                    detNota.setProducto(detOrig.getProducto());
                    detNota.setCombo(detOrig.getCombo());
                    detNota.setCantidad(detOrig.getCantidad());
                    detNota.setPrecioUnitario(detOrig.getPrecioUnitario());
                    detNota.setDescuento(detOrig.getDescuento());
                    detNota.setSubtotal(detOrig.getSubtotal());
                    detNota.setImpuesto(detOrig.getImpuesto());
                    detNota.setTotal(detOrig.getTotal());
                    detallesNota.add(detNota);
                }
            } else if (docOriginal.getVenta() != null) {
                // Si no tiene detalles de facturación, copiar de la venta
                List<DetalleVentas> detallesVenta = detalleVentasRepo
                        .findByVentaId(docOriginal.getVenta().getId());
                for (DetalleVentas dv : detallesVenta) {
                    DetalleDocumentosFacturacion detNota = new DetalleDocumentosFacturacion();
                    detNota.setProducto(dv.getProducto());
                    detNota.setCombo(dv.getCombo());
                    detNota.setCantidad(dv.getCantidad());
                    detNota.setPrecioUnitario(dv.getPrecioUnitario());
                    detNota.setDescuento(dv.getDescuento() != null ? dv.getDescuento() : BigDecimal.ZERO);
                    detNota.setSubtotal(dv.getSubtotal());
                    detNota.setImpuesto(dv.getImpuesto() != null ? dv.getImpuesto() : BigDecimal.ZERO);
                    detNota.setTotal(dv.getTotal());
                    detallesNota.add(detNota);
                }
            }

        } else if (esNotaCredito && MOTIVO_DEVOLUCION_ITEM.equals(codigoMotivo)) {
            // ── DEVOLUCIÓN PARCIAL POR ÍTEM ──
            if (request.getItems() == null || request.getItems().isEmpty()) {
                throw new IllegalArgumentException(
                        "Para devolución por ítem (motivo 07) debe seleccionar al menos un producto");
            }

            // Obtener detalles originales (de facturación o de venta)
            List<DetalleDocumentosFacturacion> detallesFacturacion =
                    detalleDocRepo.findByDocumentoFacturacionId(docOriginal.getId());

            Map<Long, BigDecimal> preciosPorProducto;
            Map<Long, BigDecimal> cantidadesOriginales;

            if (!detallesFacturacion.isEmpty()) {
                preciosPorProducto = detallesFacturacion.stream()
                        .filter(d -> d.getProducto() != null)
                        .collect(Collectors.toMap(
                                d -> d.getProducto().getId(),
                                DetalleDocumentosFacturacion::getPrecioUnitario,
                                (a, b) -> a));
                cantidadesOriginales = detallesFacturacion.stream()
                        .filter(d -> d.getProducto() != null)
                        .collect(Collectors.toMap(
                                d -> d.getProducto().getId(),
                                DetalleDocumentosFacturacion::getCantidad,
                                BigDecimal::add));
            } else if (docOriginal.getVenta() != null) {
                List<DetalleVentas> detallesVenta = detalleVentasRepo
                        .findByVentaId(docOriginal.getVenta().getId());
                preciosPorProducto = detallesVenta.stream()
                        .filter(d -> d.getProducto() != null)
                        .collect(Collectors.toMap(
                                d -> d.getProducto().getId(),
                                DetalleVentas::getPrecioUnitario,
                                (a, b) -> a));
                cantidadesOriginales = detallesVenta.stream()
                        .filter(d -> d.getProducto() != null)
                        .collect(Collectors.toMap(
                                d -> d.getProducto().getId(),
                                DetalleVentas::getCantidad,
                                BigDecimal::add));
            } else {
                throw new IllegalStateException(
                        "El documento no tiene ítems para realizar una devolución parcial");
            }

            BigDecimal sumSubtotal = BigDecimal.ZERO;
            BigDecimal sumImpuesto = BigDecimal.ZERO;
            BigDecimal sumTotal = BigDecimal.ZERO;

            for (CrearNotaCreditoRequest.ItemDevolucion item : request.getItems()) {
                if (item.getProductoId() == null) {
                    throw new IllegalArgumentException("Cada ítem de devolución debe tener productoId");
                }
                if (item.getCantidad() == null || item.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("La cantidad a devolver debe ser mayor a 0");
                }

                BigDecimal cantOriginal = cantidadesOriginales.get(item.getProductoId());
                if (cantOriginal == null) {
                    throw new IllegalArgumentException(
                            "El producto ID " + item.getProductoId() + " no está en el comprobante original");
                }
                if (item.getCantidad().compareTo(cantOriginal) > 0) {
                    throw new IllegalArgumentException(
                            "No se puede devolver más cantidad (" + item.getCantidad()
                            + ") que la vendida (" + cantOriginal + ") para el producto ID " + item.getProductoId());
                }

                BigDecimal precioUnit = preciosPorProducto.get(item.getProductoId());
                BigDecimal totalItem = item.getCantidad().multiply(precioUnit)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal subtotalItem = totalItem.divide(factorIgv, 2, RoundingMode.HALF_UP);
                BigDecimal igvItem = totalItem.subtract(subtotalItem);

                DetalleDocumentosFacturacion detNota = new DetalleDocumentosFacturacion();
                Productos prod = new Productos();
                prod.setId(item.getProductoId());
                detNota.setProducto(prod);
                detNota.setCantidad(item.getCantidad());
                detNota.setPrecioUnitario(precioUnit);
                detNota.setDescuento(BigDecimal.ZERO);
                detNota.setSubtotal(subtotalItem);
                detNota.setImpuesto(igvItem);
                detNota.setTotal(totalItem);
                detallesNota.add(detNota);

                sumSubtotal = sumSubtotal.add(subtotalItem);
                sumImpuesto = sumImpuesto.add(igvItem);
                sumTotal = sumTotal.add(totalItem);
            }

            subtotal = sumSubtotal;
            impuestos = sumImpuesto;
            total = sumTotal;

            // Validar que total devuelto no supere total del comprobante
            BigDecimal totalNcExistentes = documentosRepo
                    .sumTotalNotasCreditoByDocumentoReferenciaId(docOriginal.getId());
            if (totalNcExistentes.add(total).compareTo(docOriginal.getTotal()) > 0) {
                throw new IllegalStateException(
                        "El monto total de esta nota (S/ " + total.setScale(2, RoundingMode.HALF_UP)
                        + ") más las notas anteriores (S/ " + totalNcExistentes.setScale(2, RoundingMode.HALF_UP)
                        + ") supera el total del comprobante (S/ " + docOriginal.getTotal().setScale(2, RoundingMode.HALF_UP) + ")");
            }

            // Restaurar stock para los ítems devueltos
            restaurarStockDevolucion(docOriginal, request.getItems(),
                    usuario != null ? usuario : docOriginal.getUsuario());

        } else if (esNotaCredito && MOTIVOS_DESCUENTO.contains(codigoMotivo)) {
            // ── DESCUENTO GLOBAL / DISMINUCIÓN EN EL VALOR ──
            if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Para descuento/disminución debe ingresar un monto mayor a 0");
            }

            total = request.getMonto().setScale(2, RoundingMode.HALF_UP);

            // Validar que no supere acumulado
            BigDecimal totalNcExistentes = documentosRepo
                    .sumTotalNotasCreditoByDocumentoReferenciaId(docOriginal.getId());
            if (totalNcExistentes.add(total).compareTo(docOriginal.getTotal()) > 0) {
                BigDecimal maxPermitido = docOriginal.getTotal().subtract(totalNcExistentes);
                throw new IllegalStateException(
                        "El monto del descuento (S/ " + total + ") supera el saldo disponible (S/ "
                        + maxPermitido.setScale(2, RoundingMode.HALF_UP) + ")");
            }

            subtotal = total.divide(factorIgv, 2, RoundingMode.HALF_UP);
            impuestos = total.subtract(subtotal);

            // Crear una línea de ajuste
            DetalleDocumentosFacturacion detAjuste = new DetalleDocumentosFacturacion();
            detAjuste.setCantidad(BigDecimal.ONE);
            detAjuste.setPrecioUnitario(total);
            detAjuste.setDescuento(BigDecimal.ZERO);
            detAjuste.setSubtotal(subtotal);
            detAjuste.setImpuesto(impuestos);
            detAjuste.setTotal(total);
            detallesNota.add(detAjuste);

        } else if (!esNotaCredito) {
            // ── NOTA DE DÉBITO (cargo adicional) ──
            if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Para notas de débito debe ingresar un monto mayor a 0");
            }

            total = request.getMonto().setScale(2, RoundingMode.HALF_UP);
            subtotal = total.divide(factorIgv, 2, RoundingMode.HALF_UP);
            impuestos = total.subtract(subtotal);

            // Línea de ajuste positivo
            DetalleDocumentosFacturacion detAjuste = new DetalleDocumentosFacturacion();
            detAjuste.setCantidad(BigDecimal.ONE);
            detAjuste.setPrecioUnitario(total);
            detAjuste.setDescuento(BigDecimal.ZERO);
            detAjuste.setSubtotal(subtotal);
            detAjuste.setImpuesto(impuestos);
            detAjuste.setTotal(total);
            detallesNota.add(detAjuste);

        } else {
            throw new IllegalArgumentException("Código de motivo '" + codigoMotivo + "' no reconocido para nota de crédito");
        }

        // ── 8. Crear documento NC/ND ──
        DocumentosFacturacion doc = new DocumentosFacturacion();
        doc.setNegocio(docOriginal.getNegocio());
        doc.setSerieFacturacion(serie);
        doc.setTipoDocumento(tipoDoc);
        doc.setNumeroDocumento(numeroDocumento);
        doc.setDocumentoReferencia(docOriginal);
        doc.setCodigoMotivoNota(codigoMotivo);
        doc.setDescripcionMotivoNota(request.getDescripcionMotivo());
        doc.setVenta(docOriginal.getVenta());
        doc.setCliente(docOriginal.getCliente());
        doc.setUsuario(usuario != null ? usuario : docOriginal.getUsuario());
        doc.setFechaEmision(LocalDate.now());
        doc.setSubtotal(subtotal);
        doc.setImpuestos(impuestos);
        doc.setTotal(total);
        doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.pendiente_envio);

        boolean usarPse = Boolean.TRUE.equals(docOriginal.getNegocio().getTienePse());
        doc.setModoEmision(usarPse
                ? DocumentosFacturacion.ModoEmision.PSE
                : DocumentosFacturacion.ModoEmision.LOCAL);

        doc = documentosRepo.save(doc);

        // ── 9. Guardar detalles de la nota ──
        for (DetalleDocumentosFacturacion detalle : detallesNota) {
            detalle.setDocumentoFacturacion(doc);
            detalleDocRepo.save(detalle);
        }

        // ── 10. Si es LOCAL, aceptar directamente ──
        if (!usarPse) {
            doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.aceptado);
            doc.setRespuestaSunat("Emitido localmente (sin PSE)");
            documentosRepo.save(doc);
        }

        // ── 11. Si es anulación completa, aplicar efectos de negocio ──
        if (esNotaCredito && MOTIVOS_ANULACION_COMPLETA.contains(codigoMotivo)) {
            docOriginal.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.anulado);
            docOriginal.setMotivoAnulacion("Anulado por " + TIPO_DOC_LABELS.get(tipoDoc)
                    + " " + numeroDocumento + " (Motivo SUNAT: " + codigoMotivo + ")");
            documentosRepo.save(docOriginal);

            // 11a. Marcar la venta como anulada (si existe y no lo está ya)
            Ventas venta = docOriginal.getVenta();
            if (venta != null && venta.getEstado() != Ventas.Estado.anulada) {
                venta.setEstado(Ventas.Estado.anulada);
                venta.setCanceladoEn(LocalDateTime.now());
                venta.setRazonCancelacion("Anulada por NC " + numeroDocumento
                        + " (Motivo SUNAT: " + codigoMotivo + ")");
                if (usuario != null) {
                    venta.setCanceladoPor(usuariosRepo.getReferenceById(usuario.getId()));
                }
                ventasRepo.save(venta);

                // 11b. Restaurar stock de todos los productos de la venta
                restaurarStockVentaCompleta(venta, usuario);

                // 11c. Crear egreso en caja (solo porción efectivo)
                crearEgresoCajaPorNC(venta, total, true,
                        "Anulación por NC " + numeroDocumento);
            }
        }

        // ── 12. Si es devolución parcial (07), crear egreso en caja ──
        if (esNotaCredito && MOTIVO_DEVOLUCION_ITEM.equals(codigoMotivo)) {
            Ventas ventaDev = docOriginal.getVenta();
            if (ventaDev != null) {
                crearEgresoCajaPorNC(ventaDev, total, false,
                        "Devolución por NC " + numeroDocumento);
            }
        }

        return doc;
    }

    private static final Map<DocumentosFacturacion.TipoDocumento, String> TIPO_DOC_LABELS = Map.of(
            DocumentosFacturacion.TipoDocumento.nota_credito, "Nota de Crédito",
            DocumentosFacturacion.TipoDocumento.nota_debito, "Nota de Débito"
    );

    /**
     * Restaura stock al almacén predeterminado para los ítems devueltos en una NC motivo 07.
     */
    private void restaurarStockDevolucion(DocumentosFacturacion docOriginal,
                                           List<CrearNotaCreditoRequest.ItemDevolucion> items,
                                           Usuarios usuario) {
        if (docOriginal.getVenta() == null || docOriginal.getVenta().getSede() == null) return;

        Almacenes almacenDefault = almacenesRepo
                .findFirstBySede_IdAndEsPredeterminado(docOriginal.getVenta().getSede().getId(), true)
                .orElse(null);
        if (almacenDefault == null) return;

        for (CrearNotaCreditoRequest.ItemDevolucion item : items) {
            if (item.getProductoId() == null) continue;

            Optional<StockInventario> stockOpt = stockRepo
                    .findByProductoIdAndAlmacenIdForUpdate(item.getProductoId(), almacenDefault.getId());

            if (stockOpt.isPresent()) {
                StockInventario stock = stockOpt.get();
                stock.setCantidadActual(stock.getCantidadActual().add(item.getCantidad()));
                stock.setCantidadDisponible(stock.getCantidadDisponible().add(item.getCantidad()));
                stockRepo.save(stock);
            }

            // Registrar movimiento de inventario
            MovimientosInventario mov = new MovimientosInventario();
            mov.setNegocio(docOriginal.getNegocio());
            Productos prod = new Productos();
            prod.setId(item.getProductoId());
            mov.setProducto(prod);
            mov.setAlmacenDestino(almacenDefault);
            mov.setTipoMovimiento(MovimientosInventario.TipoMovimiento.devolucion);
            mov.setCantidad(item.getCantidad());

            // Buscar precio unitario para el registro
            List<DetalleDocumentosFacturacion> detalles =
                    detalleDocRepo.findByDocumentoFacturacionId(docOriginal.getId());
            BigDecimal costo = detalles.stream()
                    .filter(d -> d.getProducto() != null && d.getProducto().getId().equals(item.getProductoId()))
                    .findFirst()
                    .map(DetalleDocumentosFacturacion::getPrecioUnitario)
                    .orElse(BigDecimal.ZERO);

            mov.setCostoUnitario(costo);
            mov.setMontoTotal(item.getCantidad().multiply(costo).setScale(2, RoundingMode.HALF_UP));
            mov.setMotivoMovimiento("Devolución por NC - " + docOriginal.getNumeroDocumento());
            mov.setReferenciaDocumento(docOriginal.getNumeroDocumento());
            mov.setUsuario(usuario);
            mov.setFechaMovimiento(LocalDateTime.now());
            movInventarioRepo.save(mov);
        }
    }
    /**
     * Restaura stock completo de una venta anulada por NC (motivos 01/02/03/06).
     * Devuelve todos los productos al almacén predeterminado de la sede.
     */
    private void restaurarStockVentaCompleta(Ventas venta, Usuarios usuario) {
        if (venta.getSede() == null) return;

        List<DetalleVentas> detalles = detalleVentasRepo.findByVentaId(venta.getId());
        if (detalles.isEmpty()) return;

        Almacenes almacenDefault = almacenesRepo
                .findFirstBySede_IdAndEsPredeterminado(venta.getSede().getId(), true)
                .orElse(null);
        if (almacenDefault == null) return;

        Usuarios usuarioOp = usuario != null ? usuario
                : (venta.getCanceladoPor() != null ? venta.getCanceladoPor() : venta.getUsuario());

        for (DetalleVentas detalle : detalles) {
            if (detalle.getProducto() == null) continue;
            BigDecimal cantidadARestaurar = detalle.getCantidad();

            Optional<StockInventario> stockOpt = stockRepo
                    .findByProductoIdAndAlmacenIdForUpdate(detalle.getProducto().getId(), almacenDefault.getId());
            if (stockOpt.isPresent()) {
                StockInventario stock = stockOpt.get();
                stock.setCantidadActual(stock.getCantidadActual().add(cantidadARestaurar));
                stock.setCantidadDisponible(stock.getCantidadDisponible().add(cantidadARestaurar));
                stockRepo.save(stock);
            }

            MovimientosInventario movInv = new MovimientosInventario();
            movInv.setNegocio(venta.getNegocio());
            movInv.setProducto(detalle.getProducto());
            movInv.setAlmacenDestino(almacenDefault);
            movInv.setTipoMovimiento(MovimientosInventario.TipoMovimiento.devolucion);
            movInv.setCantidad(cantidadARestaurar);
            movInv.setCostoUnitario(detalle.getPrecioUnitario());
            movInv.setMontoTotal(cantidadARestaurar.multiply(detalle.getPrecioUnitario())
                    .setScale(2, RoundingMode.HALF_UP));
            movInv.setMotivoMovimiento("Anulación por NC - Venta " + venta.getNumeroVenta());
            movInv.setReferenciaDocumento(venta.getNumeroVenta());
            movInv.setUsuario(usuarioOp);
            movInv.setFechaMovimiento(LocalDateTime.now());
            movInventarioRepo.save(movInv);
        }
    }

    /**
     * Crea un egreso de caja por NC (anulación o devolución).
     * Solo afecta la porción en efectivo del pago original.
     *
     * @param venta                 La venta asociada
     * @param montoNC               Monto total de la NC
     * @param esAnulacionCompleta   Si true, egresa todo el efectivo; si false, proporcional
     * @param descripcion           Descripción del movimiento
     */
    private void crearEgresoCajaPorNC(Ventas venta, BigDecimal montoNC,
                                       boolean esAnulacionCompleta, String descripcion) {
        if (venta.getSesionCaja() == null
                || venta.getSesionCaja().getEstadoSesion() != SesionesCaja.EstadoSesion.abierta) {
            return;
        }

        BigDecimal totalEfectivo = BigDecimal.ZERO;
        List<PagosVenta> pagos = pagosVentaRepo.findByVentaId(venta.getId());
        for (PagosVenta pago : pagos) {
            MetodosPago mp = pago.getMetodoPago();
            if (mp != null && mp.getTipo() == MetodosPago.TipoMetodoPago.efectivo) {
                totalEfectivo = totalEfectivo.add(
                        pago.getMonto() != null ? pago.getMonto() : BigDecimal.ZERO);
            }
        }

        if (totalEfectivo.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal montoEgreso;
        if (esAnulacionCompleta) {
            montoEgreso = totalEfectivo;
        } else {
            if (venta.getTotal().compareTo(BigDecimal.ZERO) <= 0) return;
            montoEgreso = montoNC.multiply(totalEfectivo)
                    .divide(venta.getTotal(), 2, RoundingMode.HALF_UP);
        }

        if (montoEgreso.compareTo(BigDecimal.ZERO) <= 0) return;

        MovimientosCaja movimiento = new MovimientosCaja();
        movimiento.setSesionCaja(venta.getSesionCaja());
        movimiento.setTipoMovimiento(MovimientosCaja.TipoMovimiento.egreso_anulacion);
        movimiento.setMonto(montoEgreso);
        movimiento.setDescripcion(descripcion);
        movimiento.setVenta(venta);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimientosRepo.save(movimiento);
    }
    // ═══════════════════════════════════════════════════════════════════
    //  ANULACIÓN DE COMPROBANTES
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Obtiene los comprobantes asociados a una venta (lectura).
     */
    @Transactional(readOnly = true)
    public List<DocumentosFacturacion> getDocumentosByVentaId(Long ventaId) {
        return documentosRepo.findByVentaId(ventaId);
    }

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
     * Marca un documento como {@code enviado} (en proceso de envío a SUNAT).
     * Valida que el estado actual sea {@code pendiente_envio} o {@code rechazado}.
     *
     * @param documentoId ID del documento a marcar
     * @return el documento actualizado
     */
    @Transactional
    public DocumentosFacturacion marcarComoEnviando(Long documentoId) {
        DocumentosFacturacion doc = documentosRepo.findById(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));

        if (doc.getEstadoDocumento() != DocumentosFacturacion.EstadoDocumento.pendiente_envio
                && doc.getEstadoDocumento() != DocumentosFacturacion.EstadoDocumento.rechazado) {
            throw new IllegalStateException(
                    "Solo se pueden enviar documentos en estado pendiente o rechazado");
        }

        doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.enviado);
        return documentosRepo.save(doc);
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

        Long negocioId = doc.getNegocio().getId();
        PseProvider provider = pseProviderFactory.getProvider(negocioId);
        RespuestaPse respuesta = provider.enviarDocumento(doc);

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

        doc = documentosRepo.save(doc);

        // Registrar en historial de comunicaciones PSE
        String proveedorNombre = pseProviderFactory.getProveedorActual(negocioId);
        registrarHistorial(doc, "REENVIO", respuesta, proveedorNombre, negocioId);

        return doc;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  HISTORIAL PSE
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Registra un intento de comunicación con el PSE en el historial.
     */
    private void registrarHistorial(DocumentosFacturacion doc, String tipoOperacion,
                                     RespuestaPse respuesta, String proveedor, Long negocioId) {
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
            historial.setProveedor(proveedor);

            // Obtener entorno de la configuración
            configPseRepo.findFirstByNegocioId(negocioId)
                    .ifPresent(config -> historial.setEntorno(
                            config.getEntorno() != null ? config.getEntorno() : "SANDBOX"));

            historialPseRepo.save(historial);
        } catch (Exception e) {
            // No fallar la operación principal por un error en el log
            System.err.println("Error al registrar historial PSE: " + e.getMessage());
        }
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

        // Para facturas, intentar crear/buscar cliente a partir del RUC del documento
        if (venta.getTipoComprobante() == Ventas.TipoComprobante.factura) {
            String rucDoc = venta.getDocClienteNumero();
            if (rucDoc == null || rucDoc.isBlank()) {
                throw new IllegalStateException("La factura requiere un RUC de cliente");
            }

            Long negocioId = venta.getNegocio().getId();

            // Buscar cliente existente con ese RUC en el mismo negocio
            Optional<Clientes> existente = clientesRepo
                    .findFirstByNegocioIdAndNumeroDocumento(negocioId, rucDoc);
            if (existente.isPresent()) {
                Clientes c = existente.get();
                // Actualizar nombre y dirección si vienen en la venta
                boolean modified = false;
                if (venta.getDocClienteNombre() != null && !venta.getDocClienteNombre().isBlank()) {
                    c.setRazonSocial(venta.getDocClienteNombre());
                    modified = true;
                }
                if (venta.getDocClienteDireccion() != null && !venta.getDocClienteDireccion().isBlank()) {
                    c.setDireccion(venta.getDocClienteDireccion());
                    modified = true;
                }
                if (modified) {
                    clientesRepo.save(c);
                }
                return c;
            }

            // Crear cliente temporal para este comprobante
            Clientes nuevo = new Clientes();
            nuevo.setNegocio(venta.getNegocio());
            nuevo.setTipoDocumento(Clientes.TipoDocumento.RUC);
            nuevo.setNumeroDocumento(rucDoc);
            String nombreRuc = venta.getDocClienteNombre() != null
                    ? venta.getDocClienteNombre() : rucDoc;
            nuevo.setRazonSocial(nombreRuc);
            if (venta.getDocClienteDireccion() != null) {
                nuevo.setDireccion(venta.getDocClienteDireccion());
            }
            return clientesRepo.save(nuevo);
        }

        // Para boletas sin cliente — verificar si viene DNI/número de documento
        Long negocioId = venta.getNegocio().getId();
        String docNum = venta.getDocClienteNumero();
        String docNombre = venta.getDocClienteNombre();

        if (docNum != null && !docNum.isBlank() && !docNum.equals("00000000")) {
            // Viene con DNI: buscar o crear cliente por ese documento
            Optional<Clientes> existente = clientesRepo
                    .findFirstByNegocioIdAndNumeroDocumento(negocioId, docNum);
            if (existente.isPresent()) {
                Clientes c = existente.get();
                // Actualizar nombre si se proporcionó uno
                if (docNombre != null && !docNombre.isBlank()) {
                    String[] partes = docNombre.trim().split(" ", 2);
                    c.setNombres(partes[0]);
                    c.setApellidos(partes.length > 1 ? partes[1] : "");
                    clientesRepo.save(c);
                }
                return c;
            }
            // No existe: crear con el DNI. Si no hay nombre, usar "Cliente General"
            Clientes nuevo = new Clientes();
            nuevo.setNegocio(venta.getNegocio());
            nuevo.setTipoDocumento(Clientes.TipoDocumento.DNI);
            nuevo.setNumeroDocumento(docNum);
            if (docNombre != null && !docNombre.isBlank()) {
                String[] partes = docNombre.trim().split(" ", 2);
                nuevo.setNombres(partes[0]);
                nuevo.setApellidos(partes.length > 1 ? partes[1] : "");
            } else {
                nuevo.setNombres("Cliente");
                nuevo.setApellidos("General");
            }
            return clientesRepo.save(nuevo);
        }

        // Sin DNI → buscar o crear el cliente genérico "Consumidor Final"
        Optional<Clientes> genericoOpt = clientesRepo
                .findFirstByNegocioIdAndNumeroDocumento(negocioId, "00000000");

        if (genericoOpt.isPresent()) {
            return genericoOpt.get();
        }

        // Crear cliente genérico
        Clientes consumidorFinal = new Clientes();
        consumidorFinal.setNegocio(venta.getNegocio());
        consumidorFinal.setTipoDocumento(Clientes.TipoDocumento.DNI);
        consumidorFinal.setNumeroDocumento("00000000");
        consumidorFinal.setNombres("Consumidor");
        consumidorFinal.setApellidos("Final");
        return clientesRepo.save(consumidorFinal);
    }
}
