package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.*;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.service.DocumentoFacturacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de documentos de facturación electrónica.
 * Base: /restful/facturacion
 *
 * Endpoints:
 * - GET    /restful/facturacion/documentos                           - Listar/buscar documentos
 * - POST   /restful/facturacion/documentos                           - Emitir nuevo documento
 * - POST   /restful/facturacion/documentos/desde-pedido/{pedidoId}   - Emitir desde un pedido
 * - GET    /restful/facturacion/{id}                                 - Obtener documento por ID
 * - GET    /restful/facturacion/{id}/estado                          - Consultar estado
 * - POST   /restful/facturacion/{id}/enviar                          - Enviar a SUNAT (simulado)
 * - POST   /restful/facturacion/{id}/anular                          - Anular documento
 * - GET    /restful/facturacion/{id}/pdf                             - Generar/obtener PDF simulado
 */
@RestController
@RequestMapping("/restful/facturacion")
@CrossOrigin(origins = "*")
public class DocumentoFacturacionController {

    @Autowired
    private DocumentoFacturacionService documentoService;

    /**
     * Listar/buscar documentos con filtros avanzados.
     * Todos los filtros son opcionales excepto negocioId.
     */
    @GetMapping("/documentos")
    public ResponseEntity<?> listarDocumentos(
            @RequestParam(name = "negocioId", required = true) Long negocioId,
            @RequestParam(name = "sedeId", required = false) Long sedeId,
            @RequestParam(name = "tipoDocumento", required = false) String tipoDocumento,
            @RequestParam(name = "estado", required = false) String estado,
            @RequestParam(name = "estadoSunat", required = false) String estadoSunat,
            @RequestParam(name = "fechaDesde", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(name = "fechaHasta", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(name = "numeroCompleto", required = false) String numeroCompleto) {
        try {
            List<DocumentoFacturacionResponse> documentos = documentoService.buscarDocumentos(
                    negocioId, sedeId, tipoDocumento, estado, estadoSunat,
                    fechaDesde, fechaHasta, numeroCompleto);
            return ResponseEntity.ok(documentos);
        } catch (OperacionInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al buscar documentos de facturación"));
        }
    }

    /**
     * Emitir un nuevo documento de facturación.
     * Tipo: boleta, factura, nota_credito, nota_debito, guia_remision.
     */
    @PostMapping("/documentos")
    public ResponseEntity<?> emitirDocumento(@RequestBody CreateDocumentoFacturacionRequest request) {
        try {
            DocumentoFacturacionResponse documento = documentoService.emitirDocumento(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(documento);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (OperacionInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al emitir documento de facturación: " + e.getMessage()));
        }
    }

    /**
     * Emitir documento de facturación a partir de un pedido existente.
     * Genera boleta/factura tomando los datos del pedido (items, cliente, montos).
     *
     * Request body ejemplo:
     * {
     *   "tipoDocumento": "boleta",      // opcional, se puede dejar en la URL
     *   "rucEmisor": "20123456789",
     *   "razonSocialEmisor": "Mi Licorería SAC",
     *   "nombreReceptor": "Juan Pérez", // opcional, se toma del pedido si no se envía
     *   "creadoPor": 3
     * }
     */
    @PostMapping("/documentos/desde-pedido/{pedidoId}")
    public ResponseEntity<?> emitirDesdePedido(
            @PathVariable Long pedidoId,
            @RequestParam(name = "negocioId", required = true) Long negocioId,
            @RequestParam(name = "serieId", required = true) Long serieId,
            @RequestParam(name = "tipoDocumento", defaultValue = "boleta") String tipoDocumento,
            @RequestBody CreateDocumentoFacturacionRequest request) {
        try {
            DocumentoFacturacionResponse documento = documentoService.emitirDesdePedido(
                    negocioId, pedidoId, serieId, tipoDocumento, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(documento);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (OperacionInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al emitir documento desde pedido: " + e.getMessage()));
        }
    }

    /**
     * Obtener un documento completo por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDocumento(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId) {
        try {
            DocumentoFacturacionResponse documento = documentoService.obtenerPorId(id, negocioId);
            return ResponseEntity.ok(documento);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener el documento de facturación"));
        }
    }

    /**
     * Consultar estado de un documento (resumen rápido).
     */
    @GetMapping("/{id}/estado")
    public ResponseEntity<?> consultarEstado(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId) {
        try {
            DocumentoFacturacionResponse documento = documentoService.consultarEstado(id, negocioId);
            return ResponseEntity.ok(Map.of(
                    "documentoId", documento.getId(),
                    "numeroCompleto", documento.getNumeroCompleto(),
                    "tipoDocumento", documento.getTipoDocumento(),
                    "estado", documento.getEstado(),
                    "estadoSunat", documento.getEstadoSunat(),
                    "codigoRespuestaSunat", documento.getCodigoRespuestaSunat() != null
                            ? documento.getCodigoRespuestaSunat() : "",
                    "mensajeRespuestaSunat", documento.getMensajeRespuestaSunat() != null
                            ? documento.getMensajeRespuestaSunat() : "",
                    "total", documento.getTotal(),
                    "fechaEmision", documento.getFechaEmision()
            ));
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al consultar estado del documento"));
        }
    }

    /**
     * Enviar documento a SUNAT vía PSE (MODO SIMULADO).
     * Simula el envío y devuelve respuesta simulada.
     */
    @PostMapping("/{id}/enviar")
    public ResponseEntity<?> enviarASunat(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId) {
        try {
            EnvioSunatResponse resultado = documentoService.enviarASunat(id, negocioId);
            return ResponseEntity.ok(resultado);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (OperacionInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al enviar documento a SUNAT: " + e.getMessage()));
        }
    }

    /**
     * Anular un documento de facturación.
     * No elimina el registro, solo actualiza estado.
     * Si el documento fue aceptado por SUNAT, se envía Comunicación de Baja vía PSE.
     */
    @PostMapping("/{id}/anular")
    public ResponseEntity<?> anularDocumento(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId,
            @RequestBody AnularDocumentoRequest request) {
        try {
            DocumentoFacturacionResponse documento = documentoService.anularDocumento(id, negocioId, request);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Documento anulado correctamente",
                    "documento", documento));
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (OperacionInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al anular documento de facturación"));
        }
    }

    /**
     * PUT /restful/facturacion/{id} - Actualizar documento de facturación
     * Solo se pueden editar documentos en estado 'borrador'
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDocumento(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId,
            @RequestBody CreateDocumentoFacturacionRequest request) {
        try {
            DocumentoFacturacionResponse documento = documentoService.actualizar(id, negocioId, request);
            return ResponseEntity.ok(documento);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (OperacionInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar documento de facturación"));
        }
    }

    /**
     * DELETE /restful/facturacion/{id} - Eliminar documento de facturación
     * Solo se pueden eliminar documentos en estado 'borrador'
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDocumento(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId) {
        try {
            documentoService.eliminar(id, negocioId);
            return ResponseEntity.noContent().build();
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (OperacionInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar documento de facturación"));
        }
    }

    /**
     * Generar/obtener PDF simulado del documento.
     * Retorna URL simulada de descarga.
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<?> obtenerPdf(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId) {
        try {
            DocumentoFacturacionResponse documento = documentoService.generarPdf(id, negocioId);
            return ResponseEntity.ok(Map.of(
                    "documentoId", documento.getId(),
                    "numeroCompleto", documento.getNumeroCompleto(),
                    "urlPdf", documento.getUrlPdfSunat() != null ? documento.getUrlPdfSunat() : "",
                    "mensaje", "PDF generado exitosamente [SIMULACIÓN]"
            ));
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al generar PDF del documento"));
        }
    }
}
