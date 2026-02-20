package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.*;
import DrinkGo.DrinkGo_backend.entity.DetalleDocumentoFacturacion;
import DrinkGo.DrinkGo_backend.entity.DocumentoFacturacion;
import DrinkGo.DrinkGo_backend.entity.Pedido;
import DrinkGo.DrinkGo_backend.entity.PedidoItem;
import DrinkGo.DrinkGo_backend.entity.SerieFacturacion;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.integration.pse.PseClient;
import DrinkGo.DrinkGo_backend.integration.pse.PseResponse;
import DrinkGo.DrinkGo_backend.repository.DetalleDocumentoFacturacionRepository;
import DrinkGo.DrinkGo_backend.repository.DocumentoFacturacionRepository;
import DrinkGo.DrinkGo_backend.repository.PedidoItemRepository;
import DrinkGo.DrinkGo_backend.repository.PedidoRepository;
import DrinkGo.DrinkGo_backend.repository.SerieFacturacionRepository;
import DrinkGo.DrinkGo_backend.repository.ConfiguracionGlobalPlataformaRepository;
import DrinkGo.DrinkGo_backend.entity.ConfiguracionGlobalPlataforma;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio principal para gestión de documentos de facturación electrónica.
 * Multi-tenant: todas las operaciones filtran por negocio_id.
 *
 * Responsabilidades:
 * - Emisión de documentos (boletas, facturas, notas de crédito/débito)
 * - Generación de documentos desde pedidos (asociación con ventas)
 * - Cálculo de impuestos: IGV (18%) + ISC (bebidas alcohólicas)
 * - Generación de número correlativo atómico (con bloqueo pesimista)
 * - Envío simulado a SUNAT vía PSE desacoplado
 * - Anulación de documentos (Comunicación de Baja / Resumen Diario vía PSE)
 * - Consulta avanzada con filtros
 * - Generación simulada de PDF
 *
 * REGLAS LEGALES SUNAT APLICADAS:
 * - Series: B* para boletas, F* para facturas, BC*FC* para notas crédito, etc.
 * - Boletas >= S/ 700: requieren documento de identidad del receptor (DNI)
 * - Notas crédito/débito: solo pueden referenciar documentos del mismo tipo (boleta→boleta, factura→factura)
 * - Facturas: requieren RUC del receptor (11 dígitos)
 * - Anulación de documentos aceptados por SUNAT: requiere Comunicación de Baja vía PSE
 * - ISC: campo explícito por ítem para bebidas alcohólicas, se suma a la base IGV
 * - Precio unitario: sin IGV ni ISC (base imponible), debe ser > 0
 * - IGV configurable desde configuración global de plataforma (clave: TASA_IGV, default: 18%)
 *   Cumple RF-CGL-001 y RF-ADM-022: impuestos configurables a nivel global/negocio
 * - Códigos SUNAT: 01=factura, 03=boleta, 07=NC, 08=ND, 09=guía remisión
 * - Boletas se envían vía Resumen Diario (simulado), facturas individualmente
 */
@Service
public class DocumentoFacturacionService {

    /** Tasa IGV por defecto si no hay configuración global definida */
    private static final BigDecimal TASA_IGV_DEFAULT = new BigDecimal("0.18");
    private static final String CLAVE_TASA_IGV = "TASA_IGV";
    private static final BigDecimal UMBRAL_BOLETA_DNI = new BigDecimal("700.00");

    @Autowired
    private DocumentoFacturacionRepository documentoRepository;

    @Autowired
    private DetalleDocumentoFacturacionRepository detalleRepository;

    @Autowired
    private SerieFacturacionRepository serieRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    @Autowired
    private PseClient pseClient;

    @Autowired
    private ConfiguracionGlobalPlataformaRepository configuracionRepository;

    // ==================================================================================
    // EMISIÓN DE DOCUMENTOS
    // ==================================================================================

    /**
     * Emitir un nuevo documento de facturación.
     * - Valida serie activa, prefijo correcto según tipo, consistencia multi-tenant.
     * - Incrementa correlativo con bloqueo pesimista (evita race condition).
     * - Calcula impuestos: ISC + IGV (configurable desde plataforma) sobre (base + ISC).
     * - Guarda snapshot del emisor.
     * - Estado inicial: emitido / pendiente.
     */
    @Transactional
    public DocumentoFacturacionResponse emitirDocumento(CreateDocumentoFacturacionRequest request) {
        // --- Validaciones obligatorias ---
        validarRequestEmision(request);

        // --- Obtener tasa IGV configurable ---
        BigDecimal tasaIgv = obtenerTasaIgv();

        // --- Validar tipo de documento ---
        DocumentoFacturacion.TipoDocumento tipoDoc;
        try {
            tipoDoc = DocumentoFacturacion.TipoDocumento.valueOf(request.getTipoDocumento());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException("Tipo de documento inválido: " + request.getTipoDocumento()
                    + ". Valores permitidos: boleta, factura, nota_credito, nota_debito, guia_remision");
        }

        // --- Validar y obtener serie CON BLOQUEO PESIMISTA ---
        SerieFacturacion serie = serieRepository.findByIdAndNegocioIdConBloqueo(
                        request.getSerieId(), request.getNegocioId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Serie de facturación", request.getSerieId()));

        if (!serie.getEstaActivo()) {
            throw new OperacionInvalidaException(
                    "La serie '" + serie.getPrefijoSerie() + "' está desactivada");
        }

        // Validar consistencia multi-tenant: serie pertenece a la misma sede
        if (!serie.getSedeId().equals(request.getSedeId())) {
            throw new OperacionInvalidaException(
                    "La serie no pertenece a la sede indicada. "
                            + "Serie sede_id=" + serie.getSedeId()
                            + ", request sede_id=" + request.getSedeId());
        }

        // Validar que el tipo de documento coincida con la serie
        if (!serie.getTipoDocumento().name().equals(tipoDoc.name())) {
            throw new OperacionInvalidaException(
                    "El tipo de documento '" + tipoDoc.name()
                            + "' no coincide con la serie '" + serie.getPrefijoSerie()
                            + "' que es de tipo '" + serie.getTipoDocumento().name() + "'");
        }

        // --- Validar prefijo de serie según normativa SUNAT ---
        validarPrefijoSerie(serie.getPrefijoSerie(), tipoDoc);

        // --- Validación para notas de crédito/débito ---
        if (tipoDoc == DocumentoFacturacion.TipoDocumento.nota_credito
                || tipoDoc == DocumentoFacturacion.TipoDocumento.nota_debito) {
            validarNotaReferencia(request, tipoDoc);
        }

        // --- Incrementar correlativo (ya con bloqueo pesimista) ---
        int nuevoCorrelativo = serie.getNumeroActual() + 1;
        serie.setNumeroActual(nuevoCorrelativo);
        serieRepository.save(serie);

        // --- Generar número completo: PREFIJO-CORRELATIVO (formato 8 dígitos) ---
        String numeroCompleto = serie.getPrefijoSerie() + "-"
                + String.format("%08d", nuevoCorrelativo);

        // --- Procesar detalles y calcular montos con ISC ---
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new OperacionInvalidaException(
                    "El documento debe tener al menos un item de detalle");
        }

        List<DetalleDocumentoFacturacion> detallesEntidad = new ArrayList<>();
        BigDecimal subtotalCalculado = BigDecimal.ZERO;
        BigDecimal totalDescuentoCalculado = BigDecimal.ZERO;
        BigDecimal totalGravadoCalculado = BigDecimal.ZERO;
        BigDecimal totalIgvCalculado = BigDecimal.ZERO;
        BigDecimal totalIscCalculado = BigDecimal.ZERO;

        int itemNum = 1;
        for (DetalleDocumentoFacturacionRequest detReq : request.getDetalles()) {
            validarDetalleRequest(detReq, itemNum);

            // Calcular montos del item
            BigDecimal subtotalItem = detReq.getCantidad()
                    .multiply(detReq.getPrecioUnitario())
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal descuentoItem = detReq.getMontoDescuento() != null
                    ? detReq.getMontoDescuento() : BigDecimal.ZERO;

            // Validar que descuento no exceda subtotal
            if (descuentoItem.compareTo(subtotalItem) > 0) {
                throw new OperacionInvalidaException(
                        "El descuento del item " + itemNum
                                + " (S/ " + descuentoItem + ") no puede exceder "
                                + "su subtotal (S/ " + subtotalItem + ")");
            }

            BigDecimal montoGravadoItem = subtotalItem.subtract(descuentoItem)
                    .setScale(2, RoundingMode.HALF_UP);

            // ISC del item (obligatorio para bebidas alcohólicas)
            BigDecimal iscItem = detReq.getMontoIsc() != null
                    ? detReq.getMontoIsc() : BigDecimal.ZERO;

            // IGV se calcula sobre (base imponible + ISC) según norma SUNAT
            // Tasa configurable desde ConfiguracionGlobalPlataforma (clave: TASA_IGV)
            BigDecimal baseIgv = montoGravadoItem.add(iscItem);
            BigDecimal igvItem = baseIgv.multiply(tasaIgv)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalItem = baseIgv.add(igvItem)
                    .setScale(2, RoundingMode.HALF_UP);

            DetalleDocumentoFacturacion detalle = new DetalleDocumentoFacturacion();
            detalle.setProductoId(detReq.getProductoId());
            detalle.setNumeroItem(itemNum);
            detalle.setDescripcion(detReq.getDescripcion());
            detalle.setCodigoUnidad(
                    detReq.getCodigoUnidad() != null ? detReq.getCodigoUnidad() : "NIU");
            detalle.setCantidad(detReq.getCantidad());
            detalle.setPrecioUnitario(detReq.getPrecioUnitario());
            detalle.setMontoDescuento(descuentoItem);
            detalle.setMontoGravado(montoGravadoItem);
            detalle.setMontoIgv(igvItem);
            detalle.setMontoIsc(iscItem);
            detalle.setTotal(totalItem);

            detallesEntidad.add(detalle);

            subtotalCalculado = subtotalCalculado.add(subtotalItem);
            totalDescuentoCalculado = totalDescuentoCalculado.add(descuentoItem);
            totalGravadoCalculado = totalGravadoCalculado.add(montoGravadoItem);
            totalIscCalculado = totalIscCalculado.add(iscItem);
            totalIgvCalculado = totalIgvCalculado.add(igvItem);

            itemNum++;
        }

        BigDecimal totalCalculado = totalGravadoCalculado
                .add(totalIscCalculado)
                .add(totalIgvCalculado)
                .setScale(2, RoundingMode.HALF_UP);

        // --- Validar regla boleta >= S/ 700 requiere DNI ---
        if (tipoDoc == DocumentoFacturacion.TipoDocumento.boleta
                && totalCalculado.compareTo(UMBRAL_BOLETA_DNI) >= 0) {
            if (request.getNumeroDocumentoReceptor() == null
                    || request.getNumeroDocumentoReceptor().trim().isEmpty()) {
                throw new OperacionInvalidaException(
                        "Para boletas con monto total >= S/ 700.00, el documento "
                                + "de identidad del receptor (DNI) es obligatorio "
                                + "(Reglamento de Comprobantes de Pago, Art. 4 numeral 3)");
            }
        }

        // --- Crear documento ---
        DocumentoFacturacion documento = new DocumentoFacturacion();
        documento.setNegocioId(request.getNegocioId());
        documento.setSedeId(request.getSedeId());
        documento.setSerieId(serie.getId());
        documento.setVentaId(request.getVentaId());
        documento.setPedidoId(request.getPedidoId());
        documento.setTipoDocumento(tipoDoc);
        documento.setSerie(serie.getPrefijoSerie());
        documento.setNumeroCorrelativo(nuevoCorrelativo);
        documento.setNumeroCompleto(numeroCompleto);

        // Snapshot emisor
        documento.setRucEmisor(request.getRucEmisor());
        documento.setRazonSocialEmisor(request.getRazonSocialEmisor());
        documento.setDireccionEmisor(request.getDireccionEmisor());

        // Receptor
        documento.setTipoDocumentoReceptor(request.getTipoDocumentoReceptor());
        documento.setNumeroDocumentoReceptor(request.getNumeroDocumentoReceptor());
        documento.setNombreReceptor(request.getNombreReceptor());
        documento.setDireccionReceptor(request.getDireccionReceptor());
        documento.setEmailReceptor(request.getEmailReceptor());

        // Montos calculados
        documento.setSubtotal(subtotalCalculado);
        documento.setTotalDescuento(totalDescuentoCalculado);
        documento.setTotalGravado(totalGravadoCalculado);
        documento.setTotalIgv(totalIgvCalculado);
        documento.setTotalIsc(totalIscCalculado);
        documento.setTasaIgv(tasaIgv.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));
        documento.setTotalOtrosImpuestos(BigDecimal.ZERO);
        documento.setTotal(totalCalculado);
        documento.setMoneda(request.getMoneda() != null ? request.getMoneda() : "PEN");
        documento.setTipoCambio(request.getTipoCambio());

        // Estado inicial
        documento.setEstado(DocumentoFacturacion.EstadoDocumento.emitido);
        documento.setEstadoSunat(DocumentoFacturacion.EstadoSunat.pendiente);

        // Fechas
        documento.setFechaEmision(
                request.getFechaEmision() != null ? request.getFechaEmision() : LocalDate.now());
        documento.setFechaVencimiento(request.getFechaVencimiento());

        // Referencia (notas crédito/débito)
        documento.setDocumentoReferenciadoId(request.getDocumentoReferenciadoId());
        documento.setMotivoReferencia(request.getMotivoReferencia());

        // Creador
        documento.setCreadoPor(request.getCreadoPor());

        // Guardar documento
        DocumentoFacturacion documentoGuardado = documentoRepository.save(documento);

        // Guardar detalles con documentoId
        for (DetalleDocumentoFacturacion detalle : detallesEntidad) {
            detalle.setDocumentoId(documentoGuardado.getId());
        }
        detalleRepository.saveAll(detallesEntidad);

        List<DetalleDocumentoFacturacion> detallesGuardados =
                detalleRepository.findByDocumentoIdOrderByNumeroItemAsc(
                        documentoGuardado.getId());

        return convertirAResponse(documentoGuardado, detallesGuardados);
    }

    // ==================================================================================
    // EMISIÓN DESDE PEDIDO (ASOCIACIÓN CON VENTAS)
    // ==================================================================================

    /**
     * Emitir un documento de facturación a partir de un pedido existente.
     * Toma los datos del pedido (items, cliente, montos) y genera la boleta/factura.
     *
     * @param negocioId   ID del negocio (multi-tenant)
     * @param pedidoId    ID del pedido a facturar
     * @param serieId     ID de la serie a usar
     * @param tipoDocumento "boleta" o "factura"
     * @param request     Datos adicionales del emisor y receptor
     */
    @Transactional
    public DocumentoFacturacionResponse emitirDesdePedido(
            Long negocioId, Long pedidoId, Long serieId,
            String tipoDocumento, CreateDocumentoFacturacionRequest request) {

        // Buscar pedido validando multi-tenant
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .filter(p -> p.getTenantId().equals(negocioId))
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", pedidoId));

        // Verificar que el pedido no esté cancelado/anulado
        String estadoPedido = pedido.getEstado().name();
        if ("anulado".equals(estadoPedido) || "cancelado".equals(estadoPedido)) {
            throw new OperacionInvalidaException(
                    "No se puede facturar un pedido con estado: " + estadoPedido);
        }

        // Verificar que no exista ya un documento activo para este pedido
        List<DocumentoFacturacion> docsExistentes =
                documentoRepository.findByNegocioIdAndPedidoIdAndEstadoNot(
                        negocioId, pedidoId, DocumentoFacturacion.EstadoDocumento.anulado);

        if (!docsExistentes.isEmpty()) {
            throw new OperacionInvalidaException(
                    "El pedido " + pedido.getNumeroPedido()
                            + " ya tiene un documento de facturación activo: "
                            + docsExistentes.get(0).getNumeroCompleto());
        }

        // Cargar items del pedido
        List<PedidoItem> itemsPedido = pedidoItemRepository.findByPedidoId(pedidoId);
        if (itemsPedido.isEmpty()) {
            throw new OperacionInvalidaException(
                    "El pedido no tiene items para facturar");
        }

        // Construir detalles desde items del pedido
        List<DetalleDocumentoFacturacionRequest> detalles = itemsPedido.stream()
                .map(item -> {
                    DetalleDocumentoFacturacionRequest det = new DetalleDocumentoFacturacionRequest();
                    det.setProductoId(item.getProductoId());
                    det.setDescripcion(item.getNombreProducto() != null
                            ? item.getNombreProducto() : "Producto ID " + item.getProductoId());
                    det.setCodigoUnidad("NIU");
                    det.setCantidad(item.getCantidad());
                    det.setPrecioUnitario(item.getPrecioUnitario());
                    det.setMontoDescuento(item.getDescuento() != null
                            ? item.getDescuento() : BigDecimal.ZERO);
                    det.setMontoIsc(BigDecimal.ZERO); // ISC por defecto 0, puede configurarse por producto
                    return det;
                })
                .collect(Collectors.toList());

        // Completar el request con datos del pedido
        request.setNegocioId(negocioId);
        request.setSedeId(pedido.getSedeId());
        request.setSerieId(serieId);
        request.setPedidoId(pedidoId);
        request.setTipoDocumento(tipoDocumento);
        request.setDetalles(detalles);

        // Datos del receptor desde pedido si no se proporcionaron
        if (request.getNombreReceptor() == null || request.getNombreReceptor().trim().isEmpty()) {
            request.setNombreReceptor(pedido.getClienteNombre() != null
                    ? pedido.getClienteNombre() : "Cliente general");
        }

        // Emitir usando el flujo estándar
        return emitirDocumento(request);
    }

    // ==================================================================================
    // ENVÍO A SUNAT (SIMULADO)
    // ==================================================================================

    /**
     * Enviar documento a SUNAT mediante PSE (modo simulación).
     * Actualiza estado_sunat y campos relacionados según la respuesta del PSE.
     */
    @Transactional
    public EnvioSunatResponse enviarASunat(Long documentoId, Long negocioId) {
        DocumentoFacturacion documento = documentoRepository
                .findByIdAndNegocioId(documentoId, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Documento de facturación", documentoId));

        // Validar que esté en estado válido para enviar
        if (documento.getEstado() == DocumentoFacturacion.EstadoDocumento.anulado) {
            throw new OperacionInvalidaException(
                    "No se puede enviar un documento anulado");
        }
        if (documento.getEstadoSunat() == DocumentoFacturacion.EstadoSunat.aceptado) {
            throw new OperacionInvalidaException(
                    "El documento ya fue aceptado por SUNAT");
        }

        // Invocar PSE desacoplado
        PseResponse pseResponse = pseClient.enviarDocumento(documento);

        // Actualizar documento según respuesta
        documento.setEnviadoSunatEn(LocalDateTime.now());
        documento.setTicketSunat(pseResponse.getTicketSunat());
        documento.setCodigoRespuestaSunat(pseResponse.getCodigoRespuesta());
        documento.setMensajeRespuestaSunat(pseResponse.getMensajeRespuesta());

        if (pseResponse.isExito()) {
            documento.setEstadoSunat(DocumentoFacturacion.EstadoSunat.aceptado);
            documento.setEstado(DocumentoFacturacion.EstadoDocumento.aceptado);
            documento.setAceptadoSunatEn(LocalDateTime.now());
            documento.setHashSunat(pseResponse.getHashDocumento());
            documento.setUrlXmlSunat(pseResponse.getUrlXml());
            documento.setUrlCdrSunat(pseResponse.getUrlCdr());
        } else {
            documento.setEstadoSunat(DocumentoFacturacion.EstadoSunat.rechazado);
            documento.setEstado(DocumentoFacturacion.EstadoDocumento.error);
        }

        documentoRepository.save(documento);

        // Construir response
        EnvioSunatResponse response = new EnvioSunatResponse();
        response.setDocumentoId(documento.getId());
        response.setNumeroCompleto(documento.getNumeroCompleto());
        response.setEstadoSunat(documento.getEstadoSunat().name());
        response.setCodigoRespuesta(pseResponse.getCodigoRespuesta());
        response.setMensajeRespuesta(pseResponse.getMensajeRespuesta());
        response.setHashSunat(pseResponse.getHashDocumento());
        response.setUrlXml(pseResponse.getUrlXml());
        response.setUrlCdr(pseResponse.getUrlCdr());
        response.setMensaje(pseResponse.isExito()
                ? "Documento enviado y aceptado por SUNAT vía "
                        + (documento.getTipoDocumento() == DocumentoFacturacion.TipoDocumento.boleta
                                ? "Resumen Diario" : "envío individual")
                        + " [SIMULACIÓN - PSE: " + pseClient.getNombreProveedor() + "]"
                : "Documento rechazado por SUNAT [SIMULACIÓN - PSE: "
                        + pseClient.getNombreProveedor() + "]");

        return response;
    }

    // ==================================================================================
    // ANULACIÓN (CON COMUNICACIÓN DE BAJA VÍA PSE)
    // ==================================================================================

    /**
     * Anular un documento de facturación.
     *
     * REGLA LEGAL SUNAT:
     * - Si el documento YA fue aceptado por SUNAT → se envía Comunicación de Baja vía PSE
     *   (facturas: máximo 7 días calendario desde la emisión).
     * - Si el documento NO fue enviado a SUNAT → se anula solo localmente.
     * - Documentos ya anulados no pueden anularse de nuevo.
     *
     * En ambos casos, no se elimina el registro, solo se actualizan estados.
     */
    @Transactional
    public DocumentoFacturacionResponse anularDocumento(
            Long documentoId, Long negocioId, AnularDocumentoRequest request) {

        DocumentoFacturacion documento = documentoRepository
                .findByIdAndNegocioId(documentoId, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Documento de facturación", documentoId));

        // Validar que no esté ya anulado
        if (documento.getEstado() == DocumentoFacturacion.EstadoDocumento.anulado) {
            throw new OperacionInvalidaException(
                    "El documento ya se encuentra anulado");
        }

        if (request.getMotivoAnulacion() == null
                || request.getMotivoAnulacion().trim().isEmpty()) {
            throw new OperacionInvalidaException(
                    "El motivo de anulación es obligatorio");
        }

        // Si el documento fue ACEPTADO por SUNAT → requiere anulación vía PSE
        // Facturas: Comunicación de Baja | Boletas: Resumen Diario de Baja
        if (documento.getEstadoSunat() == DocumentoFacturacion.EstadoSunat.aceptado) {
            boolean esFactura = documento.getTipoDocumento() == DocumentoFacturacion.TipoDocumento.factura;
            String mecanismo = esFactura ? "Comunicación de Baja" : "Resumen Diario de Baja";

            // Validar plazo: facturas máx 7 días desde emisión
            if (esFactura) {
                long diasDesdeEmision = java.time.temporal.ChronoUnit.DAYS.between(
                        documento.getFechaEmision(), LocalDate.now());
                if (diasDesdeEmision > 7) {
                    throw new OperacionInvalidaException(
                            "No se puede anular una factura aceptada por SUNAT después de 7 "
                                    + "días calendario desde su emisión (han pasado "
                                    + diasDesdeEmision + " días). "
                                    + "Debe emitir una Nota de Crédito en su lugar.");
                }
            }

            // Enviar anulación vía PSE (Comunicación de Baja o Resumen Diario de Baja)
            PseResponse pseAnulacion = pseClient.anularDocumento(
                    documento, request.getMotivoAnulacion());

            documento.setMensajeRespuestaSunat(pseAnulacion.getMensajeRespuesta());
            documento.setCodigoRespuestaSunat(pseAnulacion.getCodigoRespuesta());

            if (!pseAnulacion.isExito()) {
                throw new OperacionInvalidaException(
                        "SUNAT rechazó la " + mecanismo + ": "
                                + pseAnulacion.getMensajeRespuesta()
                                + " [SIMULACIÓN]. Debe emitir una Nota de Crédito.");
            }
        }

        // Actualizar estados
        documento.setEstado(DocumentoFacturacion.EstadoDocumento.anulado);
        documento.setEstadoSunat(DocumentoFacturacion.EstadoSunat.anulado);
        documento.setAnuladoEn(LocalDateTime.now());
        documento.setAnuladoPor(request.getAnuladoPor());
        documento.setMotivoAnulacion(request.getMotivoAnulacion());

        DocumentoFacturacion documentoAnulado = documentoRepository.save(documento);
        List<DetalleDocumentoFacturacion> detalles =
                detalleRepository.findByDocumentoIdOrderByNumeroItemAsc(
                        documentoAnulado.getId());
        return convertirAResponse(documentoAnulado, detalles);
    }

    // ==================================================================================
    // CONSULTAS
    // ==================================================================================

    /**
     * Obtener documentos con filtros avanzados.
     */
    @Transactional(readOnly = true)
    public List<DocumentoFacturacionResponse> buscarDocumentos(
            Long negocioId, Long sedeId, String tipoDocumento,
            String estado, String estadoSunat,
            LocalDate fechaDesde, LocalDate fechaHasta, String numeroCompleto) {

        DocumentoFacturacion.TipoDocumento tipoDoc = null;
        DocumentoFacturacion.EstadoDocumento estadoDoc = null;
        DocumentoFacturacion.EstadoSunat estadoSnt = null;

        if (tipoDocumento != null && !tipoDocumento.trim().isEmpty()) {
            try {
                tipoDoc = DocumentoFacturacion.TipoDocumento.valueOf(tipoDocumento);
            } catch (IllegalArgumentException e) {
                throw new OperacionInvalidaException(
                        "Tipo de documento inválido: " + tipoDocumento);
            }
        }
        if (estado != null && !estado.trim().isEmpty()) {
            try {
                estadoDoc = DocumentoFacturacion.EstadoDocumento.valueOf(estado);
            } catch (IllegalArgumentException e) {
                throw new OperacionInvalidaException(
                        "Estado de documento inválido: " + estado);
            }
        }
        if (estadoSunat != null && !estadoSunat.trim().isEmpty()) {
            try {
                estadoSnt = DocumentoFacturacion.EstadoSunat.valueOf(estadoSunat);
            } catch (IllegalArgumentException e) {
                throw new OperacionInvalidaException(
                        "Estado SUNAT inválido: " + estadoSunat);
            }
        }

        List<DocumentoFacturacion> documentos = documentoRepository.buscarDocumentos(
                negocioId, sedeId, tipoDoc, estadoDoc, estadoSnt,
                fechaDesde, fechaHasta, numeroCompleto);

        return documentos.stream()
                .map(doc -> {
                    List<DetalleDocumentoFacturacion> detalles =
                            detalleRepository.findByDocumentoIdOrderByNumeroItemAsc(
                                    doc.getId());
                    return convertirAResponse(doc, detalles);
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtener un documento por ID.
     */
    @Transactional(readOnly = true)
    public DocumentoFacturacionResponse obtenerPorId(Long id, Long negocioId) {
        DocumentoFacturacion documento = documentoRepository
                .findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Documento de facturación", id));

        List<DetalleDocumentoFacturacion> detalles =
                detalleRepository.findByDocumentoIdOrderByNumeroItemAsc(
                        documento.getId());
        return convertirAResponse(documento, detalles);
    }

    /**
     * Consultar estado de un documento (resumen rápido).
     */
    @Transactional(readOnly = true)
    public DocumentoFacturacionResponse consultarEstado(Long id, Long negocioId) {
        return obtenerPorId(id, negocioId);
    }
    
    /**
     * Actualizar documento de facturación (solo en estado borrador).
     */
    @Transactional
    public DocumentoFacturacionResponse actualizar(Long id, Long negocioId, CreateDocumentoFacturacionRequest request) {
        DocumentoFacturacion documento = documentoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento de facturación", id));
        
        // Solo se pueden editar documentos en estado borrador
        if (documento.getEstado() != DocumentoFacturacion.EstadoDocumento.borrador) {
            throw new OperacionInvalidaException("Solo se pueden editar documentos en estado borrador");
        }
        
        // Actualizar campos permitidos
        if (request.getNombreReceptor() != null) {
            documento.setNombreReceptor(request.getNombreReceptor());
        }
        if (request.getNumeroDocumentoReceptor() != null) {
            documento.setNumeroDocumentoReceptor(request.getNumeroDocumentoReceptor());
        }
        if (request.getDireccionReceptor() != null) {
            documento.setDireccionReceptor(request.getDireccionReceptor());
        }
        
        documentoRepository.save(documento);
        
        List<DetalleDocumentoFacturacion> detalles = 
                detalleRepository.findByDocumentoIdOrderByNumeroItemAsc(documento.getId());
        return convertirAResponse(documento, detalles);
    }
    
    /**
     * Eliminar documento de facturación (solo en estado borrador).
     */
    @Transactional
    public void eliminar(Long id, Long negocioId) {
        DocumentoFacturacion documento = documentoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento de facturación", id));
        
        // Solo se pueden eliminar documentos en estado borrador
        if (documento.getEstado() != DocumentoFacturacion.EstadoDocumento.borrador) {
            throw new OperacionInvalidaException("Solo se pueden eliminar documentos en estado borrador");
        }
        
        // Eliminar detalles primero
        detalleRepository.deleteByDocumentoId(documento.getId());
        
        // Eliminar documento
        documentoRepository.delete(documento);
    }

    // ==================================================================================
    // REIMPRESIÓN / PDF SIMULADO
    // ==================================================================================

    /**
     * Generar URL de PDF simulada para reimpresión.
     */
    @Transactional
    public DocumentoFacturacionResponse generarPdf(Long id, Long negocioId) {
        DocumentoFacturacion documento = documentoRepository
                .findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Documento de facturación", id));

        // Generar URL simulada de PDF
        String urlPdf = "https://drinkgo-facturacion.local/pdf/"
                + documento.getNegocioId()
                + "/" + documento.getNumeroCompleto() + ".pdf";
        documento.setUrlPdfSunat(urlPdf);
        documentoRepository.save(documento);

        List<DetalleDocumentoFacturacion> detalles =
                detalleRepository.findByDocumentoIdOrderByNumeroItemAsc(
                        documento.getId());
        return convertirAResponse(documento, detalles);
    }

    // ==================================================================================
    // VALIDACIONES PRIVADAS
    // ==================================================================================

    private void validarRequestEmision(CreateDocumentoFacturacionRequest request) {
        if (request.getNegocioId() == null)
            throw new OperacionInvalidaException("El negocioId es obligatorio");
        if (request.getSedeId() == null)
            throw new OperacionInvalidaException("El sedeId es obligatorio");
        if (request.getSerieId() == null)
            throw new OperacionInvalidaException("El serieId es obligatorio");
        if (request.getTipoDocumento() == null
                || request.getTipoDocumento().trim().isEmpty())
            throw new OperacionInvalidaException("El tipoDocumento es obligatorio");
        if (request.getRucEmisor() == null
                || request.getRucEmisor().trim().isEmpty())
            throw new OperacionInvalidaException("El rucEmisor es obligatorio");
        if (request.getRazonSocialEmisor() == null
                || request.getRazonSocialEmisor().trim().isEmpty())
            throw new OperacionInvalidaException(
                    "La razonSocialEmisor es obligatoria");
        if (request.getNombreReceptor() == null
                || request.getNombreReceptor().trim().isEmpty())
            throw new OperacionInvalidaException(
                    "El nombreReceptor es obligatorio");

        // Validar RUC del receptor para facturas (11 dígitos numéricos, empieza con 10 o 20)
        if ("factura".equals(request.getTipoDocumento())) {
            if (request.getNumeroDocumentoReceptor() == null
                    || request.getNumeroDocumentoReceptor().trim().isEmpty()) {
                throw new OperacionInvalidaException(
                        "Para facturas, el RUC del receptor es obligatorio");
            }
            String ruc = request.getNumeroDocumentoReceptor().trim();
            if (ruc.length() != 11) {
                throw new OperacionInvalidaException(
                        "Para facturas, el RUC del receptor debe tener 11 dígitos");
            }
            if (!ruc.matches("\\d{11}")) {
                throw new OperacionInvalidaException(
                        "El RUC del receptor debe contener solo dígitos numéricos");
            }
            if (!ruc.startsWith("10") && !ruc.startsWith("20")
                    && !ruc.startsWith("15") && !ruc.startsWith("17")) {
                throw new OperacionInvalidaException(
                        "El RUC del receptor debe comenzar con 10 (persona natural), "
                                + "20 (persona jurídica), 15 o 17 (otros contribuyentes). "
                                + "RUC proporcionado: " + ruc);
            }
            // tipoDocumentoReceptor debe ser "6" (RUC) para facturas
            if (request.getTipoDocumentoReceptor() == null
                    || !"6".equals(request.getTipoDocumentoReceptor().trim())) {
                throw new OperacionInvalidaException(
                        "Para facturas, el tipoDocumentoReceptor debe ser '6' (RUC)");
            }
        }
    }

    /**
     * Valida que el prefijo de serie cumpla con la normativa SUNAT.
     * Boletas: B*, Facturas: F*, NC de boleta: B*, NC de factura: F*,
     * ND de boleta: B*, ND de factura: F*, Guía remisión: T*.
     */
    private void validarPrefijoSerie(String prefijo,
                                      DocumentoFacturacion.TipoDocumento tipoDoc) {
        String primeraLetra = prefijo.substring(0, 1).toUpperCase();
        switch (tipoDoc) {
            case boleta:
                if (!"B".equals(primeraLetra)) {
                    throw new OperacionInvalidaException(
                            "Las series de boletas deben comenzar con 'B' "
                                    + "(ej: B001). Serie actual: " + prefijo);
                }
                break;
            case factura:
                if (!"F".equals(primeraLetra)) {
                    throw new OperacionInvalidaException(
                            "Las series de facturas deben comenzar con 'F' "
                                    + "(ej: F001). Serie actual: " + prefijo);
                }
                break;
            case guia_remision:
                if (!"T".equals(primeraLetra)) {
                    throw new OperacionInvalidaException(
                            "Las series de guías de remisión deben comenzar con 'T' "
                                    + "(ej: T001). Serie actual: " + prefijo);
                }
                break;
            case nota_credito:
            case nota_debito:
                // NC/ND pueden ser B* (de boleta) o F* (de factura)
                if (!"B".equals(primeraLetra) && !"F".equals(primeraLetra)) {
                    throw new OperacionInvalidaException(
                            "Las series de notas de crédito/débito deben comenzar "
                                    + "con 'B' o 'F'. Serie actual: " + prefijo);
                }
                break;
        }
    }

    private void validarNotaReferencia(CreateDocumentoFacturacionRequest request,
                                        DocumentoFacturacion.TipoDocumento tipoDoc) {
        if (request.getDocumentoReferenciadoId() == null) {
            throw new OperacionInvalidaException(
                    "Para " + tipoDoc.name() + " se requiere documentoReferenciadoId");
        }
        if (request.getMotivoReferencia() == null
                || request.getMotivoReferencia().trim().isEmpty()) {
            throw new OperacionInvalidaException(
                    "Para " + tipoDoc.name() + " se requiere motivoReferencia");
        }

        // Validar que el documento referenciado exista y pertenezca al mismo negocio
        DocumentoFacturacion docRef = documentoRepository
                .findByIdAndNegocioId(
                        request.getDocumentoReferenciadoId(),
                        request.getNegocioId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Documento referenciado",
                        request.getDocumentoReferenciadoId()));

        if (docRef.getEstado() == DocumentoFacturacion.EstadoDocumento.anulado) {
            throw new OperacionInvalidaException(
                    "No se puede referenciar un documento anulado");
        }

        // Validar compatibilidad de tipos: NC/ND de factura solo puede
        // referenciar facturas, NC/ND de boleta solo puede referenciar boletas
        DocumentoFacturacion.TipoDocumento tipoRef = docRef.getTipoDocumento();
        String prefijoSerie = request.getSerieId() != null
                ? serieRepository.findByIdAndNegocioId(
                        request.getSerieId(), request.getNegocioId())
                .map(s -> s.getPrefijoSerie().substring(0, 1).toUpperCase())
                .orElse("")
                : "";

        if ("F".equals(prefijoSerie)
                && tipoRef != DocumentoFacturacion.TipoDocumento.factura) {
            throw new OperacionInvalidaException(
                    "Una nota con serie F* solo puede referenciar facturas, "
                            + "pero el documento referenciado es de tipo: "
                            + tipoRef.name());
        }
        if ("B".equals(prefijoSerie)
                && tipoRef != DocumentoFacturacion.TipoDocumento.boleta) {
            throw new OperacionInvalidaException(
                    "Una nota con serie B* solo puede referenciar boletas, "
                            + "pero el documento referenciado es de tipo: "
                            + tipoRef.name());
        }
    }

    private void validarDetalleRequest(
            DetalleDocumentoFacturacionRequest detalle, int itemNum) {
        if (detalle.getDescripcion() == null
                || detalle.getDescripcion().trim().isEmpty()) {
            throw new OperacionInvalidaException(
                    "La descripción del item " + itemNum + " es obligatoria");
        }
        if (detalle.getCantidad() == null
                || detalle.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperacionInvalidaException(
                    "La cantidad del item " + itemNum + " debe ser mayor a cero");
        }
        if (detalle.getPrecioUnitario() == null
                || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperacionInvalidaException(
                    "El precio unitario del item " + itemNum
                            + " debe ser mayor a cero (precio sin IGV ni ISC)");
        }
    }

    // ==================================================================================
    // CONVERSIÓN A RESPONSE
    // ==================================================================================

    private DocumentoFacturacionResponse convertirAResponse(
            DocumentoFacturacion doc, List<DetalleDocumentoFacturacion> detalles) {

        DocumentoFacturacionResponse response = new DocumentoFacturacionResponse();
        response.setId(doc.getId());
        response.setNegocioId(doc.getNegocioId());
        response.setSedeId(doc.getSedeId());
        response.setSerieId(doc.getSerieId());
        response.setVentaId(doc.getVentaId());
        response.setPedidoId(doc.getPedidoId());
        response.setTipoDocumento(
                doc.getTipoDocumento() != null ? doc.getTipoDocumento().name() : null);
        response.setCodigoTipoDocumentoSunat(
                doc.getTipoDocumento() != null ? getCodigoTipoDocumentoSunat(doc.getTipoDocumento()) : null);
        response.setSerie(doc.getSerie());
        response.setNumeroCorrelativo(doc.getNumeroCorrelativo());
        response.setNumeroCompleto(doc.getNumeroCompleto());

        // Emisor
        response.setRucEmisor(doc.getRucEmisor());
        response.setRazonSocialEmisor(doc.getRazonSocialEmisor());
        response.setDireccionEmisor(doc.getDireccionEmisor());

        // Receptor
        response.setTipoDocumentoReceptor(doc.getTipoDocumentoReceptor());
        response.setNumeroDocumentoReceptor(doc.getNumeroDocumentoReceptor());
        response.setNombreReceptor(doc.getNombreReceptor());
        response.setDireccionReceptor(doc.getDireccionReceptor());
        response.setEmailReceptor(doc.getEmailReceptor());

        // Montos
        response.setSubtotal(doc.getSubtotal());
        response.setTotalDescuento(doc.getTotalDescuento());
        response.setTotalGravado(doc.getTotalGravado());
        response.setTotalIgv(doc.getTotalIgv());
        response.setTotalIsc(doc.getTotalIsc());
        response.setTasaIgv(doc.getTasaIgv());
        response.setTotalOtrosImpuestos(doc.getTotalOtrosImpuestos());
        response.setTotal(doc.getTotal());
        response.setMoneda(doc.getMoneda());
        response.setTipoCambio(doc.getTipoCambio());

        // SUNAT
        response.setEstadoSunat(
                doc.getEstadoSunat() != null ? doc.getEstadoSunat().name() : null);
        response.setTicketSunat(doc.getTicketSunat());
        response.setCodigoRespuestaSunat(doc.getCodigoRespuestaSunat());
        response.setMensajeRespuestaSunat(doc.getMensajeRespuestaSunat());
        response.setHashSunat(doc.getHashSunat());
        response.setUrlXmlSunat(doc.getUrlXmlSunat());
        response.setUrlCdrSunat(doc.getUrlCdrSunat());
        response.setUrlPdfSunat(doc.getUrlPdfSunat());
        response.setEnviadoSunatEn(doc.getEnviadoSunatEn());
        response.setAceptadoSunatEn(doc.getAceptadoSunatEn());

        // Referencia
        response.setDocumentoReferenciadoId(doc.getDocumentoReferenciadoId());
        response.setMotivoReferencia(doc.getMotivoReferencia());

        // Estado/Fechas
        response.setFechaEmision(doc.getFechaEmision());
        response.setFechaVencimiento(doc.getFechaVencimiento());
        response.setEstado(
                doc.getEstado() != null ? doc.getEstado().name() : null);
        response.setCreadoPor(doc.getCreadoPor());
        response.setAnuladoPor(doc.getAnuladoPor());
        response.setAnuladoEn(doc.getAnuladoEn());
        response.setMotivoAnulacion(doc.getMotivoAnulacion());
        response.setCreadoEn(doc.getCreadoEn());
        response.setActualizadoEn(doc.getActualizadoEn());

        // Detalles
        if (detalles != null) {
            List<DetalleDocumentoFacturacionResponse> detallesResp = detalles.stream()
                    .map(this::convertirDetalleAResponse)
                    .collect(Collectors.toList());
            response.setDetalles(detallesResp);
        }

        return response;
    }

    private DetalleDocumentoFacturacionResponse convertirDetalleAResponse(
            DetalleDocumentoFacturacion det) {
        DetalleDocumentoFacturacionResponse resp =
                new DetalleDocumentoFacturacionResponse();
        resp.setId(det.getId());
        resp.setDocumentoId(det.getDocumentoId());
        resp.setProductoId(det.getProductoId());
        resp.setNumeroItem(det.getNumeroItem());
        resp.setDescripcion(det.getDescripcion());
        resp.setCodigoUnidad(det.getCodigoUnidad());
        resp.setCantidad(det.getCantidad());
        resp.setPrecioUnitario(det.getPrecioUnitario());
        resp.setMontoDescuento(det.getMontoDescuento());
        resp.setMontoGravado(det.getMontoGravado());
        resp.setMontoIgv(det.getMontoIgv());
        resp.setMontoIsc(det.getMontoIsc());
        resp.setTotal(det.getTotal());
        return resp;
    }

    // ==================================================================================
    // UTILIDADES: IGV CONFIGURABLE Y CÓDIGOS SUNAT
    // ==================================================================================

    /**
     * Obtiene la tasa de IGV desde la configuración global de la plataforma.
     * Busca la clave "TASA_IGV" en configuracion_global_plataforma.
     * Si no existe, usa el valor por defecto (18% = 0.18).
     *
     * RF-CGL-001: Configurar Parámetros Globales de la Plataforma
     * RF-ADM-022: El sistema permite definir parámetros como impuestos
     *
     * Para cambiar la tasa IGV globalmente:
     * POST /restful/configuracion con { claveConfiguracion: "TASA_IGV", valorConfiguracion: "18", tipoDato: "decimal", tipoValor: "porcentaje" }
     * El valor se almacena como porcentaje (ej: 18 = 18%), se convierte internamente a decimal (0.18).
     */
    private BigDecimal obtenerTasaIgv() {
        try {
            return configuracionRepository.findByClaveConfiguracion(CLAVE_TASA_IGV)
                    .map(config -> {
                        String valor = config.getValorConfiguracion();
                        if (valor == null || valor.trim().isEmpty()) {
                            return TASA_IGV_DEFAULT;
                        }
                        BigDecimal porcentaje = new BigDecimal(valor.trim());
                        // Si el valor es >= 1, se asume que es porcentaje (ej: 18) → dividir entre 100
                        // Si es < 1, se asume que ya es decimal (ej: 0.18)
                        if (porcentaje.compareTo(BigDecimal.ONE) >= 0) {
                            return porcentaje.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                        }
                        return porcentaje;
                    })
                    .orElse(TASA_IGV_DEFAULT);
        } catch (Exception e) {
            // Si hay error al leer config (formato inválido, etc.), usar default
            return TASA_IGV_DEFAULT;
        }
    }

    /**
     * Mapea el tipo de documento interno al código SUNAT oficial.
     * Códigos según Catálogo N° 01 de SUNAT:
     * - 01: Factura
     * - 03: Boleta de Venta
     * - 07: Nota de Crédito
     * - 08: Nota de Débito
     * - 09: Guía de Remisión
     */
    private String getCodigoTipoDocumentoSunat(DocumentoFacturacion.TipoDocumento tipo) {
        switch (tipo) {
            case factura:       return "01";
            case boleta:        return "03";
            case nota_credito:  return "07";
            case nota_debito:   return "08";
            case guia_remision: return "09";
            default:            return null;
        }
    }
}
