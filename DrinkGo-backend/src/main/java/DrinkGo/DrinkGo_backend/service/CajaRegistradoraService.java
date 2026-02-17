package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.CajaRegistradoraDTO;
import DrinkGo.DrinkGo_backend.dto.CajaRegistradoraRequest;
import DrinkGo.DrinkGo_backend.entity.CajaRegistradora;
import DrinkGo.DrinkGo_backend.entity.Sede;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.repository.CajaRegistradoraRepository;
import DrinkGo.DrinkGo_backend.repository.SedeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de cajas registradoras (RF-VEN-001).
 * Bloque 8 - Ventas, POS y Cajas.
 * Todas las operaciones filtran por negocio_id (multi-tenant).
 */
@Service
public class CajaRegistradoraService {

    private final CajaRegistradoraRepository cajaRepository;
    private final SedeRepository sedeRepository;

    public CajaRegistradoraService(CajaRegistradoraRepository cajaRepository,
            SedeRepository sedeRepository) {
        this.cajaRepository = cajaRepository;
        this.sedeRepository = sedeRepository;
    }

    /** Listar todas las cajas del negocio */
    public List<CajaRegistradoraDTO> listar(Long negocioId) {
        return cajaRepository.findByNegocioId(negocioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar cajas por sede */
    public List<CajaRegistradoraDTO> listarPorSede(Long negocioId, Long sedeId) {
        return cajaRepository.findByNegocioIdAndSedeId(negocioId, sedeId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar cajas activas */
    public List<CajaRegistradoraDTO> listarActivas(Long negocioId) {
        return cajaRepository.findByNegocioIdAndEstaActivo(negocioId, true).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Obtener caja por ID */
    public CajaRegistradoraDTO obtenerPorId(Long negocioId, Long id) {
        CajaRegistradora caja = cajaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Caja registradora", id));
        return convertirADTO(caja);
    }

    /** Crear caja registradora */
    @Transactional
    public CajaRegistradoraDTO crear(Long negocioId, CajaRegistradoraRequest request) {
        // Validar que la sede pertenece al negocio
        Sede sede = sedeRepository.findByIdAndTenantId(request.getSedeId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sede", request.getSedeId()));

        // Validar que el código no esté duplicado
        if (cajaRepository.findByNegocioIdAndCodigoCaja(negocioId, request.getCodigoCaja()).isPresent()) {
            throw new OperacionInvalidaException("Ya existe una caja con el código: " + request.getCodigoCaja());
        }

        CajaRegistradora caja = new CajaRegistradora();
        caja.setNegocioId(negocioId);
        caja.setSedeId(request.getSedeId());
        caja.setNombreCaja(request.getNombreCaja());
        caja.setCodigoCaja(request.getCodigoCaja());
        caja.setEstaActivo(request.getEstaActivo() != null ? request.getEstaActivo() : true);

        cajaRepository.save(caja);
        return convertirADTO(caja);
    }

    /** Actualizar caja registradora */
    @Transactional
    public CajaRegistradoraDTO actualizar(Long negocioId, Long id, CajaRegistradoraRequest request) {
        CajaRegistradora caja = cajaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Caja registradora", id));

        // Validar sede si cambió
        if (request.getSedeId() != null && !request.getSedeId().equals(caja.getSedeId())) {
            sedeRepository.findByIdAndTenantId(request.getSedeId(), negocioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Sede", request.getSedeId()));
            caja.setSedeId(request.getSedeId());
        }

        // Validar código único si cambió
        if (request.getCodigoCaja() != null && !request.getCodigoCaja().equals(caja.getCodigoCaja())) {
            if (cajaRepository.findByNegocioIdAndCodigoCaja(negocioId, request.getCodigoCaja()).isPresent()) {
                throw new OperacionInvalidaException("Ya existe una caja con el código: " + request.getCodigoCaja());
            }
            caja.setCodigoCaja(request.getCodigoCaja());
        }

        if (request.getNombreCaja() != null) {
            caja.setNombreCaja(request.getNombreCaja());
        }
        if (request.getEstaActivo() != null) {
            caja.setEstaActivo(request.getEstaActivo());
        }

        cajaRepository.save(caja);
        return convertirADTO(caja);
    }

    /** Eliminar caja (soft delete) */
    @Transactional
    public void eliminar(Long id, Long negocioId) {
        CajaRegistradora caja = cajaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Caja registradora", id));
        cajaRepository.delete(caja);
    }

    /** Convertir entidad a DTO */
    private CajaRegistradoraDTO convertirADTO(CajaRegistradora caja) {
        CajaRegistradoraDTO dto = new CajaRegistradoraDTO();
        dto.setId(caja.getId());
        dto.setNegocioId(caja.getNegocioId());
        dto.setSedeId(caja.getSedeId());
        if (caja.getSede() != null) {
            dto.setSedeNombre(caja.getSede().getNombre());
        }
        dto.setNombreCaja(caja.getNombreCaja());
        dto.setCodigoCaja(caja.getCodigoCaja());
        dto.setEstaActivo(caja.getEstaActivo());
        dto.setCreadoEn(caja.getCreadoEn());
        dto.setActualizadoEn(caja.getActualizadoEn());
        return dto;
    }
}
