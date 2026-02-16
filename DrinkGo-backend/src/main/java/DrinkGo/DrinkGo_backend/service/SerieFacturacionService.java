package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.*;
import DrinkGo.DrinkGo_backend.entity.SerieFacturacion;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.repository.SerieFacturacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de series de facturación.
 * Multi-tenant: todas las operaciones filtran por negocio_id.
 *
 * REGLAS LEGALES SUNAT:
 * - Boletas: prefijo debe empezar con 'B' (ej: B001)
 * - Facturas: prefijo debe empezar con 'F' (ej: F001)
 * - Notas crédito/débito: prefijo debe empezar con 'B' o 'F'
 * - Guías de remisión: prefijo debe empezar con 'T' (ej: T001)
 * - No se permite cambiar el prefijo si la serie ya tiene documentos emitidos
 */
@Service
public class SerieFacturacionService {

    @Autowired
    private SerieFacturacionRepository serieFacturacionRepository;

    /**
     * Crear una nueva serie de facturación.
     * Valida prefijo según tipo de documento (normativa SUNAT).
     */
    @Transactional
    public SerieFacturacionResponse crearSerie(CreateSerieFacturacionRequest request) {
        // Validaciones obligatorias
        if (request.getNegocioId() == null)
            throw new OperacionInvalidaException("El negocioId es obligatorio");
        if (request.getSedeId() == null)
            throw new OperacionInvalidaException("El sedeId es obligatorio");
        if (request.getTipoDocumento() == null || request.getTipoDocumento().trim().isEmpty())
            throw new OperacionInvalidaException("El tipoDocumento es obligatorio");
        if (request.getPrefijoSerie() == null || request.getPrefijoSerie().trim().isEmpty())
            throw new OperacionInvalidaException("El prefijoSerie es obligatorio");

        // Validar tipo de documento
        SerieFacturacion.TipoDocumento tipoDoc;
        try {
            tipoDoc = SerieFacturacion.TipoDocumento.valueOf(request.getTipoDocumento());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException("Tipo de documento inválido: " + request.getTipoDocumento()
                    + ". Valores permitidos: boleta, factura, nota_credito, nota_debito, guia_remision");
        }

        // Validar formato de prefijo
        String prefijo = request.getPrefijoSerie().toUpperCase().trim();
        if (prefijo.length() > 10) {
            throw new OperacionInvalidaException("El prefijo de serie no puede exceder 10 caracteres");
        }

        // Validar prefijo según tipo de documento (normativa SUNAT)
        validarPrefijoSunat(prefijo, tipoDoc);

        // Validar unicidad: negocio + sede + tipo_documento + prefijo
        boolean existe = serieFacturacionRepository.existsByNegocioIdAndSedeIdAndTipoDocumentoAndPrefijoSerie(
                request.getNegocioId(), request.getSedeId(), tipoDoc, prefijo);
        if (existe) {
            throw new OperacionInvalidaException("Ya existe una serie con prefijo '" + prefijo
                    + "' para el tipo de documento '" + tipoDoc.name()
                    + "' en la sede indicada");
        }

        // Crear la entidad
        SerieFacturacion serie = new SerieFacturacion();
        serie.setNegocioId(request.getNegocioId());
        serie.setSedeId(request.getSedeId());
        serie.setTipoDocumento(tipoDoc);
        serie.setPrefijoSerie(prefijo);
        serie.setNumeroActual(0);
        serie.setEstaActivo(true);

        SerieFacturacion serieGuardada = serieFacturacionRepository.save(serie);
        return convertirAResponse(serieGuardada);
    }

    /**
     * Obtener todas las series de un negocio, opcionalmente filtrando por sede.
     */
    @Transactional(readOnly = true)
    public List<SerieFacturacionResponse> obtenerSeries(Long negocioId, Long sedeId) {
        List<SerieFacturacion> series;
        if (sedeId != null) {
            series = serieFacturacionRepository.findByNegocioIdAndSedeId(negocioId, sedeId);
        } else {
            series = serieFacturacionRepository.findByNegocioId(negocioId);
        }
        return series.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener una serie por ID y negocio.
     */
    @Transactional(readOnly = true)
    public SerieFacturacionResponse obtenerPorId(Long id, Long negocioId) {
        SerieFacturacion serie = serieFacturacionRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Serie de facturación", id));
        return convertirAResponse(serie);
    }

    /**
     * Actualizar serie (prefijo y/o estado activo).
     * No permite modificar negocioId, sedeId ni tipoDocumento.
     * No permite cambiar prefijo si la serie ya tiene documentos emitidos.
     */
    @Transactional
    public SerieFacturacionResponse actualizarSerie(Long id, Long negocioId, UpdateSerieFacturacionRequest request) {
        SerieFacturacion serie = serieFacturacionRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Serie de facturación", id));

        if (request.getPrefijoSerie() != null && !request.getPrefijoSerie().trim().isEmpty()) {
            String nuevoPrefijo = request.getPrefijoSerie().toUpperCase().trim();
            if (nuevoPrefijo.length() > 10) {
                throw new OperacionInvalidaException(
                        "El prefijo de serie no puede exceder 10 caracteres");
            }

            // Validar prefijo según tipo de documento
            validarPrefijoSunat(nuevoPrefijo, serie.getTipoDocumento());

            // Verificar unicidad si cambió el prefijo
            if (!nuevoPrefijo.equals(serie.getPrefijoSerie())) {
                // No permitir cambiar prefijo si la serie ya tiene documentos
                if (serie.getNumeroActual() > 0) {
                    throw new OperacionInvalidaException(
                            "No se puede cambiar el prefijo de la serie '"
                                    + serie.getPrefijoSerie()
                                    + "' porque ya tiene " + serie.getNumeroActual()
                                    + " documento(s) emitido(s). "
                                    + "Desactive esta serie y cree una nueva.");
                }

                boolean existe = serieFacturacionRepository
                        .existsByNegocioIdAndSedeIdAndTipoDocumentoAndPrefijoSerie(
                                negocioId, serie.getSedeId(), serie.getTipoDocumento(), nuevoPrefijo);
                if (existe) {
                    throw new OperacionInvalidaException(
                            "Ya existe una serie con prefijo '" + nuevoPrefijo
                            + "' para el tipo de documento '" + serie.getTipoDocumento().name()
                            + "' en la sede indicada");
                }
            }
            serie.setPrefijoSerie(nuevoPrefijo);
        }

        if (request.getEstaActivo() != null) {
            serie.setEstaActivo(request.getEstaActivo());
        }

        SerieFacturacion serieActualizada = serieFacturacionRepository.save(serie);
        return convertirAResponse(serieActualizada);
    }

    /**
     * Desactivar serie (DELETE lógico).
     */
    @Transactional
    public SerieFacturacionResponse desactivarSerie(Long id, Long negocioId) {
        SerieFacturacion serie = serieFacturacionRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Serie de facturación", id));

        if (!serie.getEstaActivo()) {
            throw new OperacionInvalidaException("La serie ya se encuentra desactivada");
        }

        serie.setEstaActivo(false);
        SerieFacturacion serieDesactivada = serieFacturacionRepository.save(serie);
        return convertirAResponse(serieDesactivada);
    }

    /**
     * Valida que el prefijo de serie cumpla normativa SUNAT según tipo de documento.
     */
    private void validarPrefijoSunat(String prefijo, SerieFacturacion.TipoDocumento tipoDoc) {
        String primeraLetra = prefijo.substring(0, 1).toUpperCase();
        switch (tipoDoc) {
            case boleta:
                if (!"B".equals(primeraLetra))
                    throw new OperacionInvalidaException(
                            "Las series de boletas deben comenzar con 'B' (ej: B001). Prefijo: " + prefijo);
                break;
            case factura:
                if (!"F".equals(primeraLetra))
                    throw new OperacionInvalidaException(
                            "Las series de facturas deben comenzar con 'F' (ej: F001). Prefijo: " + prefijo);
                break;
            case guia_remision:
                if (!"T".equals(primeraLetra))
                    throw new OperacionInvalidaException(
                            "Las series de guías de remisión deben comenzar con 'T' (ej: T001). Prefijo: " + prefijo);
                break;
            case nota_credito:
            case nota_debito:
                if (!"B".equals(primeraLetra) && !"F".equals(primeraLetra))
                    throw new OperacionInvalidaException(
                            "Las series de notas crédito/débito deben comenzar con 'B' o 'F'. Prefijo: " + prefijo);
                break;
        }
    }

    // --- Conversión ---

    private SerieFacturacionResponse convertirAResponse(SerieFacturacion serie) {
        SerieFacturacionResponse response = new SerieFacturacionResponse();
        response.setId(serie.getId());
        response.setNegocioId(serie.getNegocioId());
        response.setSedeId(serie.getSedeId());
        response.setTipoDocumento(serie.getTipoDocumento() != null ? serie.getTipoDocumento().name() : null);
        response.setPrefijoSerie(serie.getPrefijoSerie());
        response.setNumeroActual(serie.getNumeroActual());
        response.setEstaActivo(serie.getEstaActivo());
        response.setCreadoEn(serie.getCreadoEn());
        response.setActualizadoEn(serie.getActualizadoEn());
        return response;
    }
}
