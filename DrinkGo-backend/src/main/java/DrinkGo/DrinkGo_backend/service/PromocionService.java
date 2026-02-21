package DrinkGo.DrinkGo_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.dto.CondicionPromocionRequest;
import DrinkGo.DrinkGo_backend.dto.PromocionCreateRequest;
import DrinkGo.DrinkGo_backend.dto.PromocionUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.CondicionPromocion;
import DrinkGo.DrinkGo_backend.entity.CondicionPromocion.TipoEntidad;
import DrinkGo.DrinkGo_backend.entity.Promocion;
import DrinkGo.DrinkGo_backend.entity.Promocion.AplicaA;
import DrinkGo.DrinkGo_backend.entity.Promocion.TipoDescuento;
import DrinkGo.DrinkGo_backend.repository.CondicionPromocionRepository;
import DrinkGo.DrinkGo_backend.repository.PromocionRepository;

/**
 * Servicio de Promociones y Descuentos - Bloque 13.
 * Implementa RF-PRO-015:
 * - CRUD completo de promociones con condiciones
 * - Validación de fechas, tipos y valores
 * - Consulta de promociones vigentes
 * - Filtrado multi-tenant por negocio_id
 * - Borrado lógico (esta_activo = 0 vía @SQLDelete)
 */
@Service
public class PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private CondicionPromocionRepository condicionRepository;

    // ============================================================
    // PROMOCIONES
    // ============================================================

    /**
     * Listar todas las promociones del negocio autenticado.
     */
    public List<Promocion> listarPromociones(Long negocioId) {
        return promocionRepository.findByNegocioId(negocioId);
    }

    /**
     * Listar solo las promociones vigentes (activas y dentro de rango de fechas).
     */
    public List<Promocion> listarPromocionesVigentes(Long negocioId) {
        return promocionRepository.findVigentes(negocioId, LocalDateTime.now());
    }

    /**
     * Obtener una promoción por ID con sus condiciones.
     */
    public Map<String, Object> obtenerPromocion(Long negocioId, Long id) {
        Promocion promocion = promocionRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException(
                        "Promoción no encontrada con id " + id + " para el negocio actual"));

        List<CondicionPromocion> condiciones = condicionRepository.findByPromocionId(id);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("promocion", promocion);
        resultado.put("condiciones", condiciones);
        return resultado;
    }

    /**
     * Crear una nueva promoción con sus condiciones.
     */
    @Transactional
    public Map<String, Object> crearPromocion(Long negocioId, Long usuarioId,
                                               PromocionCreateRequest request) {
        // Validar campos obligatorios
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new RuntimeException("El nombre de la promoción es obligatorio");
        }
        if (request.getTipoDescuento() == null || request.getTipoDescuento().isBlank()) {
            throw new RuntimeException("El tipo de descuento es obligatorio");
        }
        if (request.getValorDescuento() == null || request.getValorDescuento().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El valor del descuento debe ser mayor a 0");
        }
        if (request.getValidoDesde() == null) {
            throw new RuntimeException("La fecha 'válido desde' es obligatoria");
        }
        if (request.getValidoHasta() == null) {
            throw new RuntimeException("La fecha 'válido hasta' es obligatoria");
        }
        if (request.getValidoHasta().isBefore(request.getValidoDesde())) {
            throw new RuntimeException("La fecha 'válido hasta' debe ser posterior a 'válido desde'");
        }

        // Validar tipo de descuento
        TipoDescuento tipoDescuento = parseTipoDescuento(request.getTipoDescuento());

        // Validar porcentaje si aplica
        if (tipoDescuento == TipoDescuento.porcentaje
                && request.getValorDescuento().compareTo(new BigDecimal("100")) > 0) {
            throw new RuntimeException("El porcentaje de descuento no puede ser mayor a 100");
        }

        // Validar unicidad de código si se envía
        if (request.getCodigo() != null && !request.getCodigo().isBlank()) {
            if (promocionRepository.existsByNegocioIdAndCodigo(negocioId, request.getCodigo())) {
                throw new RuntimeException("Ya existe una promoción con código '"
                        + request.getCodigo() + "' en este negocio");
            }
        }

        // Crear la promoción
        Promocion promocion = new Promocion();
        promocion.setNegocioId(negocioId);
        promocion.setNombre(request.getNombre());
        promocion.setCodigo(request.getCodigo());
        promocion.setTipoDescuento(tipoDescuento);
        promocion.setValorDescuento(request.getValorDescuento());
        promocion.setMontoMinimoCompra(request.getMontoMinimoCompra());
        promocion.setMaxUsos(request.getMaxUsos());

        if (request.getAplicaA() != null) {
            promocion.setAplicaA(parseAplicaA(request.getAplicaA()));
        }

        promocion.setValidoDesde(request.getValidoDesde());
        promocion.setValidoHasta(request.getValidoHasta());
        promocion.setCreadoPor(usuarioId);

        promocion = promocionRepository.save(promocion);

        // Crear condiciones si se envían
        guardarCondiciones(promocion.getId(), request.getCondiciones());

        // Retornar resultado completo
        List<CondicionPromocion> condiciones = condicionRepository.findByPromocionId(promocion.getId());

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("promocion", promocion);
        resultado.put("condiciones", condiciones);
        return resultado;
    }

    /**
     * Actualizar una promoción existente.
     * Actualiza condiciones si se envían (reemplazo completo).
     */
    @Transactional
    public Map<String, Object> actualizarPromocion(Long negocioId, Long id,
                                                    PromocionUpdateRequest request) {
        Promocion promocion = promocionRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException(
                        "Promoción no encontrada con id " + id + " para el negocio actual"));

        if (request.getNombre() != null) promocion.setNombre(request.getNombre());

        if (request.getTipoDescuento() != null) {
            promocion.setTipoDescuento(parseTipoDescuento(request.getTipoDescuento()));
        }

        if (request.getValorDescuento() != null) {
            if (request.getValorDescuento().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("El valor del descuento debe ser mayor a 0");
            }
            if (promocion.getTipoDescuento() == TipoDescuento.porcentaje
                    && request.getValorDescuento().compareTo(new BigDecimal("100")) > 0) {
                throw new RuntimeException("El porcentaje de descuento no puede ser mayor a 100");
            }
            promocion.setValorDescuento(request.getValorDescuento());
        }

        if (request.getMontoMinimoCompra() != null) promocion.setMontoMinimoCompra(request.getMontoMinimoCompra());
        if (request.getMaxUsos() != null) promocion.setMaxUsos(request.getMaxUsos());

        if (request.getAplicaA() != null) {
            promocion.setAplicaA(parseAplicaA(request.getAplicaA()));
        }

        if (request.getValidoDesde() != null) promocion.setValidoDesde(request.getValidoDesde());
        if (request.getValidoHasta() != null) promocion.setValidoHasta(request.getValidoHasta());

        // Validar coherencia de fechas después de actualizar
        if (promocion.getValidoHasta().isBefore(promocion.getValidoDesde())) {
            throw new RuntimeException("La fecha 'válido hasta' debe ser posterior a 'válido desde'");
        }

        promocion = promocionRepository.save(promocion);

        // Reemplazar condiciones si se envían
        if (request.getCondiciones() != null) {
            condicionRepository.deleteByPromocionId(id);
            guardarCondiciones(id, request.getCondiciones());
        }

        List<CondicionPromocion> condiciones = condicionRepository.findByPromocionId(id);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("promocion", promocion);
        resultado.put("condiciones", condiciones);
        return resultado;
    }

    /**
     * Eliminar promoción (borrado lógico vía @SQLDelete).
     */
    @Transactional
    public void eliminarPromocion(Long negocioId, Long id) {
        Promocion promocion = promocionRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException(
                        "Promoción no encontrada con id " + id + " para el negocio actual"));
        promocionRepository.delete(promocion);
    }

    // ============================================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ============================================================

    private void guardarCondiciones(Long promocionId, List<CondicionPromocionRequest> condiciones) {
        if (condiciones == null || condiciones.isEmpty()) return;

        for (CondicionPromocionRequest req : condiciones) {
            if (req.getTipoEntidad() == null || req.getEntidadId() == null) {
                throw new RuntimeException("Cada condición debe tener tipoEntidad y entidadId");
            }

            CondicionPromocion condicion = new CondicionPromocion();
            condicion.setPromocionId(promocionId);
            condicion.setTipoEntidad(parseTipoEntidad(req.getTipoEntidad()));
            condicion.setEntidadId(req.getEntidadId());
            condicionRepository.save(condicion);
        }
    }

    private TipoDescuento parseTipoDescuento(String valor) {
        try {
            return TipoDescuento.valueOf(valor);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de descuento inválido: " + valor
                    + ". Valores permitidos: porcentaje, monto_fijo");
        }
    }

    private AplicaA parseAplicaA(String valor) {
        try {
            return AplicaA.valueOf(valor);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Valor 'aplica_a' inválido: " + valor
                    + ". Valores permitidos: todo, categoria, producto");
        }
    }

    private TipoEntidad parseTipoEntidad(String valor) {
        try {
            return TipoEntidad.valueOf(valor);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de entidad inválido: " + valor
                    + ". Valores permitidos: producto, categoria, marca");
        }
    }
}
