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

    /** Motivos NC que anulan la operación CON devolución física (restaurar stock + anular venta). */
    private static final Set<String> MOTIVOS_ANULACION_CON_DEVOLUCION = Set.of("01", "06");

    /** Motivos NC de corrección documental (anula documento, NO stock, NO anula venta). */
    private static final Set<String> MOTIVOS_CORRECCION_DOCUMENTAL = Set.of("02", "03");

    /** Todos los motivos que copian ítems completos del original (anulación + corrección). */
    private static final Set<String> MOTIVOS_ANULACION_COMPLETA = Set.of("01", "02", "03", "06");

    /** Motivo NC: devolución parcial por ítem. */
    private static final String MOTIVO_DEVOLUCION_ITEM = "07";

    /** Motivos NC: descuento/disminución (requieren monto). */
    private static final Set<String> MOTIVOS_DESCUENTO = Set.of("04", "09");

    /** Motivos NC que permiten devolución de dinero al cliente. */
    private static final Set<String> MOTIVOS_CON_DEVOLUCION_DINERO = Set.of("01", "06", "07", "09");

    /**
     * Emite una Nota de Crédito o Nota de Débito referenciando un comprobante original.
     * <p>
     * Comportamiento según motivo:
     * <ul>
     *   <li><b>NC 01/06</b>: Anulación con devolución física. Copia ítems, anula venta,
     *       restaura stock, permite devolver dinero.</li>
     *   <li><b>NC 02/03</b>: Corrección documental. Copia ítems y anula el documento,
     *       pero NO restaura stock ni anula la venta (corrección de datos).</li>
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

        // Si el negocio tiene PSE, solo se permite NC/ND sobre documentos aceptados u observados por SUNAT
        boolean usaPse = Boolean.TRUE.equals(docOriginal.getNegocio().getTienePse());
        if (usaPse) {
            DocumentosFacturacion.EstadoDocumento estado = docOriginal.getEstadoDocumento();
            if (estado != DocumentosFacturacion.EstadoDocumento.aceptado
                    && estado != DocumentosFacturacion.EstadoDocumento.observado) {
                throw new IllegalStateException(
                        "Solo se puede emitir NC/ND sobre documentos aceptados u observados por SUNAT. "
                        + "Estado actual: " + estado);
            }
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

            // Calcular cantidades ya devueltas por NC 07 anteriores
            Map<Long, BigDecimal> cantidadesYaDevueltas = calcularCantidadesDevueltas(docOriginal.getId());

            BigDecimal igvRate = tasaIgv.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

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

                // Restar cantidades ya devueltas por NC 07 anteriores
                BigDecimal yaDevueltas = cantidadesYaDevueltas.getOrDefault(item.getProductoId(), BigDecimal.ZERO);
                BigDecimal cantidadDisponible = cantOriginal.subtract(yaDevueltas);

                if (cantidadDisponible.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException(
                            "El producto ID " + item.getProductoId()
                            + " ya fue devuelto completamente en notas de crédito anteriores");
                }
                if (item.getCantidad().compareTo(cantidadDisponible) > 0) {
                    throw new IllegalArgumentException(
                            "No se puede devolver " + item.getCantidad()
                            + " unidades del producto ID " + item.getProductoId()
                            + ". Cantidad disponible: " + cantidadDisponible
                            + " (original: " + cantOriginal + ", ya devueltas: " + yaDevueltas + ")");
                }

                // precioUnit es el precio SIN IGV (valor venta unitario)
                BigDecimal precioUnit = preciosPorProducto.get(item.getProductoId());
                BigDecimal subtotalItem = item.getCantidad().multiply(precioUnit)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal igvItem = subtotalItem.multiply(igvRate)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal totalItem = subtotalItem.add(igvItem);

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

        // ── 11. Efectos de negocio según categoría de motivo ──

        // 11-A. Anulación con devolución física (01, 06): anula doc + venta + stock + dinero opcional
        if (esNotaCredito && MOTIVOS_ANULACION_CON_DEVOLUCION.contains(codigoMotivo)) {
            // Con PSE: el comprobante sigue aceptado en SUNAT, solo se marca internamente como compensado.
            // Sin PSE: se marca como anulado (documento local).
            if (usarPse) {
                docOriginal.setMotivoAnulacion("Compensado por " + TIPO_DOC_LABELS.get(tipoDoc)
                        + " " + numeroDocumento + " (Motivo SUNAT: " + codigoMotivo + ")");
            } else {
                docOriginal.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.anulado);
                docOriginal.setMotivoAnulacion("Anulado por " + TIPO_DOC_LABELS.get(tipoDoc)
                        + " " + numeroDocumento + " (Motivo SUNAT: " + codigoMotivo + ")");
            }
            documentosRepo.save(docOriginal);

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

                // Restaurar stock de todos los productos de la venta
                restaurarStockVentaCompleta(venta, usuario);

                // Crear egreso en caja SOLO si el usuario lo solicitó
                if (Boolean.TRUE.equals(request.getDevolverDinero())) {
                    crearEgresoCajaPorNC(venta, total, true,
                            "Anulación por NC " + numeroDocumento);
                }
            }
        }

        // 11-B. Corrección documental (02, 03): solo anula el documento, NO stock, NO venta
        if (esNotaCredito && MOTIVOS_CORRECCION_DOCUMENTAL.contains(codigoMotivo)) {
            // Con PSE: el comprobante sigue aceptado en SUNAT, solo se marca internamente como compensado.
            // Sin PSE: se marca como anulado (documento local).
            if (usarPse) {
                docOriginal.setMotivoAnulacion("Compensado por " + TIPO_DOC_LABELS.get(tipoDoc)
                        + " " + numeroDocumento + " (Motivo SUNAT: " + codigoMotivo + ")");
            } else {
                docOriginal.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.anulado);
                docOriginal.setMotivoAnulacion("Anulado por " + TIPO_DOC_LABELS.get(tipoDoc)
                        + " " + numeroDocumento + " (Motivo SUNAT: " + codigoMotivo + ")");
            }
            documentosRepo.save(docOriginal);
            // No se restaura stock ni se anula la venta — es corrección de datos del documento.
            // No se permite devolución de dinero para estos motivos.
        }

        // ── 12. Si es devolución parcial (07), crear egreso SOLO si solicitado ──
        if (esNotaCredito && MOTIVO_DEVOLUCION_ITEM.equals(codigoMotivo)) {
            Ventas ventaDev = docOriginal.getVenta();
            if (ventaDev != null && Boolean.TRUE.equals(request.getDevolverDinero())) {
                crearEgresoCajaPorNC(ventaDev, total, false,
                        "Devolución por NC " + numeroDocumento);
            }
        }

        // ── 13. Si es descuento/disminución (04/09), crear egreso SOLO para motivo 09 si solicitado ──
        //       Motivo 04 (descuento global) es ajuste contable, NO permite devolverDinero.
        if (esNotaCredito && "09".equals(codigoMotivo)) {
            Ventas ventaDesc = docOriginal.getVenta();
            if (ventaDesc != null && Boolean.TRUE.equals(request.getDevolverDinero())) {
                crearEgresoCajaPorNC(ventaDesc, total, false,
                        "Disminución en el valor por NC " + numeroDocumento);
            }
        }

        return doc;
    }

    private static final Map<DocumentosFacturacion.TipoDocumento, String> TIPO_DOC_LABELS = Map.of(
            DocumentosFacturacion.TipoDocumento.nota_credito, "Nota de Crédito",
            DocumentosFacturacion.TipoDocumento.nota_debito, "Nota de Débito"
    );

    // ═══════════════════════════════════════════════════════════════════
    //  REEMISIÓN DE COMPROBANTE CON NUEVO RUC (TRAS NC MOTIVO 02)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Reemite un comprobante con un nuevo RUC/documento de cliente.
     * Se usa después de emitir una NC motivo 02 (error en RUC).
     * <p>
     * Crea un nuevo comprobante (boleta o factura) con los mismos ítems y montos
     * del documento original, pero asociado al nuevo cliente.
     *
     * @param ncId           ID de la NC motivo 02 que anuló el original
     * @param tipoDocumento  Tipo de documento del nuevo cliente (RUC, DNI, CE)
     * @param numeroDocumento Número de documento del nuevo cliente
     * @param nombreCliente  Razón social o nombre del nuevo cliente
     * @param direccion      Dirección del nuevo cliente (opcional)
     * @param usuarioId      ID del usuario que realiza la operación
     * @return El nuevo comprobante emitido
     */
    @Transactional
    public DocumentosFacturacion reemitirComprobanteConNuevoCliente(
            Long ncId,
            String tipoDocumento,
            String numeroDocumento,
            String nombreCliente,
            String direccion,
            Long usuarioId) {

        // 1. Validar que el documento NC existe y es motivo 02
        DocumentosFacturacion nc = documentosRepo.findById(ncId)
                .orElseThrow(() -> new IllegalArgumentException("Nota de crédito no encontrada"));

        if (nc.getTipoDocumento() != DocumentosFacturacion.TipoDocumento.nota_credito) {
            throw new IllegalStateException("El documento no es una nota de crédito");
        }
        if (!"02".equals(nc.getCodigoMotivoNota())) {
            throw new IllegalStateException(
                    "Solo se puede reemitir desde una NC con motivo 02 (error en RUC)");
        }
        if (nc.getEstadoDocumento() == DocumentosFacturacion.EstadoDocumento.anulado) {
            throw new IllegalStateException("La nota de crédito está anulada");
        }

        // 2. Obtener el documento original (el que fue anulado por esta NC)
        DocumentosFacturacion docOriginal = nc.getDocumentoReferencia();
        if (docOriginal == null) {
            throw new IllegalStateException("No se encontró el documento original de referencia");
        }

        // 3. Verificar que no se haya reemitido ya
        List<DocumentosFacturacion> existentes = documentosRepo.findNotasByDocumentoReferenciaId(ncId);
        boolean yaReemitido = existentes.stream()
                .anyMatch(d -> (d.getTipoDocumento() == DocumentosFacturacion.TipoDocumento.boleta
                        || d.getTipoDocumento() == DocumentosFacturacion.TipoDocumento.factura)
                        && d.getEstadoDocumento() != DocumentosFacturacion.EstadoDocumento.anulado);
        if (yaReemitido) {
            throw new IllegalStateException("Ya se emitió un comprobante corregido desde esta NC");
        }

        // 4. Determinar tipo de comprobante a emitir (mismo que el original)
        DocumentosFacturacion.TipoDocumento tipoDocNuevo = docOriginal.getTipoDocumento();
        SeriesFacturacion.TipoDocumento tipoDocSerie = SeriesFacturacion.TipoDocumento
                .valueOf(tipoDocNuevo.name());

        // 5. Buscar serie predeterminada
        Long negocioId = docOriginal.getNegocio().getId();
        Optional<SeriesFacturacion> serieOpt = seriesRepo
                .findFirstByNegocioIdAndTipoDocumentoAndEsPredeterminada(negocioId, tipoDocSerie, true);
        if (serieOpt.isEmpty()) {
            throw new IllegalStateException(
                    "No hay serie predeterminada configurada para " + tipoDocNuevo
                    + ". Vaya a Facturación → Series y configure una.");
        }

        // 6. Generar correlativo
        SeriesFacturacion serie = seriesRepo.findByIdForUpdate(serieOpt.get().getId())
                .orElseThrow(() -> new RuntimeException("Serie de facturación no encontrada"));
        int correlativo = serie.getNumeroActual();
        serie.setNumeroActual(correlativo + 1);
        seriesRepo.save(serie);
        String nuevoNumeroDocumento = serie.getSerie() + "-" + String.format("%08d", correlativo);

        // 7. Buscar o crear cliente con el nuevo documento
        Clientes cliente = resolverClienteParaReemision(
                negocioId, docOriginal.getNegocio(),
                tipoDocumento, numeroDocumento, nombreCliente, direccion);

        // 8. Crear el nuevo comprobante
        Usuarios usuario = null;
        if (usuarioId != null) {
            usuario = new Usuarios();
            usuario.setId(usuarioId);
        }

        DocumentosFacturacion nuevoDoc = new DocumentosFacturacion();
        nuevoDoc.setNegocio(docOriginal.getNegocio());
        nuevoDoc.setSerieFacturacion(serie);
        nuevoDoc.setTipoDocumento(tipoDocNuevo);
        nuevoDoc.setNumeroDocumento(nuevoNumeroDocumento);
        nuevoDoc.setDocumentoReferencia(nc); // referencia la NC como origen
        nuevoDoc.setVenta(docOriginal.getVenta());
        nuevoDoc.setCliente(cliente);
        nuevoDoc.setUsuario(usuario != null ? usuario : docOriginal.getUsuario());
        nuevoDoc.setFechaEmision(LocalDate.now());
        nuevoDoc.setSubtotal(docOriginal.getSubtotal());
        nuevoDoc.setImpuestos(docOriginal.getImpuestos());
        nuevoDoc.setTotal(docOriginal.getTotal());
        nuevoDoc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.pendiente_envio);

        boolean usarPse = Boolean.TRUE.equals(docOriginal.getNegocio().getTienePse());
        nuevoDoc.setModoEmision(usarPse
                ? DocumentosFacturacion.ModoEmision.PSE
                : DocumentosFacturacion.ModoEmision.LOCAL);

        nuevoDoc = documentosRepo.save(nuevoDoc);

        // 9. Copiar detalles del documento original
        List<DetalleDocumentosFacturacion> detallesOrig =
                detalleDocRepo.findByDocumentoFacturacionId(docOriginal.getId());

        if (!detallesOrig.isEmpty()) {
            for (DetalleDocumentosFacturacion detOrig : detallesOrig) {
                DetalleDocumentosFacturacion detNuevo = new DetalleDocumentosFacturacion();
                detNuevo.setDocumentoFacturacion(nuevoDoc);
                detNuevo.setProducto(detOrig.getProducto());
                detNuevo.setCombo(detOrig.getCombo());
                detNuevo.setCantidad(detOrig.getCantidad());
                detNuevo.setPrecioUnitario(detOrig.getPrecioUnitario());
                detNuevo.setDescuento(detOrig.getDescuento());
                detNuevo.setSubtotal(detOrig.getSubtotal());
                detNuevo.setImpuesto(detOrig.getImpuesto());
                detNuevo.setTotal(detOrig.getTotal());
                detalleDocRepo.save(detNuevo);
            }
        } else if (docOriginal.getVenta() != null) {
            // Copiar desde la venta si no hay detalles de facturación
            List<DetalleVentas> detallesVenta = detalleVentasRepo
                    .findByVentaId(docOriginal.getVenta().getId());
            for (DetalleVentas dv : detallesVenta) {
                DetalleDocumentosFacturacion detNuevo = new DetalleDocumentosFacturacion();
                detNuevo.setDocumentoFacturacion(nuevoDoc);
                detNuevo.setProducto(dv.getProducto());
                detNuevo.setCombo(dv.getCombo());
                detNuevo.setCantidad(dv.getCantidad());
                detNuevo.setPrecioUnitario(dv.getPrecioUnitario());
                detNuevo.setDescuento(dv.getDescuento() != null ? dv.getDescuento() : BigDecimal.ZERO);
                detNuevo.setSubtotal(dv.getSubtotal());
                detNuevo.setImpuesto(dv.getImpuesto() != null ? dv.getImpuesto() : BigDecimal.ZERO);
                detNuevo.setTotal(dv.getTotal());
                detalleDocRepo.save(detNuevo);
            }
        }

        // 10. Si es LOCAL, aceptar directamente
        if (!usarPse) {
            nuevoDoc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.aceptado);
            nuevoDoc.setRespuestaSunat("Emitido localmente (sin PSE) — Reemisión por corrección de RUC");
            documentosRepo.save(nuevoDoc);
        }

        return nuevoDoc;
    }

    /**
     * Resuelve o crea un cliente para la reemisión de comprobante.
     */
    private Clientes resolverClienteParaReemision(
            Long negocioId, Negocios negocio,
            String tipoDocumento, String numeroDocumento,
            String nombreCliente, String direccion) {

        // Buscar cliente existente con ese documento
        Optional<Clientes> existente = clientesRepo
                .findFirstByNegocioIdAndNumeroDocumento(negocioId, numeroDocumento);

        if (existente.isPresent()) {
            Clientes c = existente.get();
            // Actualizar datos si se proporcionan
            if (nombreCliente != null && !nombreCliente.isBlank()) {
                if ("RUC".equalsIgnoreCase(tipoDocumento)) {
                    c.setRazonSocial(nombreCliente);
                } else {
                    String[] partes = nombreCliente.trim().split(" ", 2);
                    c.setNombres(partes[0]);
                    c.setApellidos(partes.length > 1 ? partes[1] : "");
                }
            }
            if (direccion != null && !direccion.isBlank()) {
                c.setDireccion(direccion);
            }
            clientesRepo.save(c);
            return c;
        }

        // Crear nuevo cliente
        Clientes nuevo = new Clientes();
        nuevo.setNegocio(negocio);

        if ("RUC".equalsIgnoreCase(tipoDocumento)) {
            nuevo.setTipoDocumento(Clientes.TipoDocumento.RUC);
            nuevo.setRazonSocial(nombreCliente != null ? nombreCliente : numeroDocumento);
        } else if ("CE".equalsIgnoreCase(tipoDocumento)) {
            nuevo.setTipoDocumento(Clientes.TipoDocumento.CE);
            if (nombreCliente != null && !nombreCliente.isBlank()) {
                String[] partes = nombreCliente.trim().split(" ", 2);
                nuevo.setNombres(partes[0]);
                nuevo.setApellidos(partes.length > 1 ? partes[1] : "");
            }
        } else {
            nuevo.setTipoDocumento(Clientes.TipoDocumento.DNI);
            if (nombreCliente != null && !nombreCliente.isBlank()) {
                String[] partes = nombreCliente.trim().split(" ", 2);
                nuevo.setNombres(partes[0]);
                nuevo.setApellidos(partes.length > 1 ? partes[1] : "");
            } else {
                nuevo.setNombres("Cliente");
                nuevo.setApellidos("General");
            }
        }

        nuevo.setNumeroDocumento(numeroDocumento);
        if (direccion != null && !direccion.isBlank()) {
            nuevo.setDireccion(direccion);
        }
        return clientesRepo.save(nuevo);
    }

    /**
     * Calcula las cantidades ya devueltas por NC motivo 07 anteriores sobre un comprobante.
     * Recorre todas las NC 07 no anuladas que referencian este documento,
     * y suma las cantidades por productoId.
     */
    private Map<Long, BigDecimal> calcularCantidadesDevueltas(Long documentoReferenciaId) {
        Map<Long, BigDecimal> devueltas = new java.util.HashMap<>();

        List<DocumentosFacturacion> notasExistentes = documentosRepo
                .findNotasByDocumentoReferenciaId(documentoReferenciaId);

        for (DocumentosFacturacion nota : notasExistentes) {
            // Solo NC motivo 07 no anuladas
            if (nota.getTipoDocumento() != DocumentosFacturacion.TipoDocumento.nota_credito) continue;
            if (!"07".equals(nota.getCodigoMotivoNota())) continue;
            if (nota.getEstadoDocumento() == DocumentosFacturacion.EstadoDocumento.anulado) continue;

            List<DetalleDocumentosFacturacion> detallesNota =
                    detalleDocRepo.findByDocumentoFacturacionId(nota.getId());
            for (DetalleDocumentosFacturacion det : detallesNota) {
                if (det.getProducto() == null) continue;
                devueltas.merge(det.getProducto().getId(), det.getCantidad(), BigDecimal::add);
            }
        }
        return devueltas;
    }

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
