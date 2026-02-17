package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.AbrirSesionCajaRequest;
import DrinkGo.DrinkGo_backend.dto.CerrarSesionCajaRequest;
import DrinkGo.DrinkGo_backend.dto.SesionCajaDTO;
import DrinkGo.DrinkGo_backend.entity.CajaRegistradora;
import DrinkGo.DrinkGo_backend.entity.SesionCaja;
import DrinkGo.DrinkGo_backend.entity.Usuario;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de sesiones de caja (RF-VEN-001..002).
 * Bloque 8 - Ventas, POS y Cajas.
 * Maneja apertura, cierre y control de turnos de caja.
 */
@Service
public class SesionCajaService {

    private final SesionCajaRepository sesionRepository;
    private final CajaRegistradoraRepository cajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;

    public SesionCajaService(SesionCajaRepository sesionRepository,
            CajaRegistradoraRepository cajaRepository,
            UsuarioRepository usuarioRepository,
            VentaRepository ventaRepository,
            MovimientoCajaRepository movimientoCajaRepository) {
        this.sesionRepository = sesionRepository;
        this.cajaRepository = cajaRepository;
        this.usuarioRepository = usuarioRepository;
        this.ventaRepository = ventaRepository;
        this.movimientoCajaRepository = movimientoCajaRepository;
    }

    /** Listar todas las sesiones del negocio */
    public List<SesionCajaDTO> listar(Long negocioId) {
        return sesionRepository.findByNegocioId(negocioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar sesiones por caja */
    public List<SesionCajaDTO> listarPorCaja(Long negocioId, Long cajaId) {
        return sesionRepository.findByNegocioIdAndCajaId(negocioId, cajaId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar sesiones abiertas */
    public List<SesionCajaDTO> listarAbiertas(Long negocioId) {
        return sesionRepository.findByNegocioIdAndEstaAbierta(negocioId, true).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Obtener sesión por ID */
    public SesionCajaDTO obtenerPorId(Long negocioId, Long id) {
        SesionCaja sesion = sesionRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión de caja", id));
        return convertirADTO(sesion);
    }

    /** Obtener sesión abierta de una caja */
    public SesionCajaDTO obtenerSesionAbierta(Long negocioId, Long cajaId) {
        SesionCaja sesion = sesionRepository.findByNegocioIdAndCajaIdAndEstaAbierta(negocioId, cajaId, true)
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay sesión abierta para la caja " + cajaId));
        return convertirADTO(sesion);
    }

    /** Abrir sesión de caja */
    @Transactional
    public SesionCajaDTO abrirSesion(AbrirSesionCajaRequest request, Long usuarioId, Long negocioId) {
        // Validar que la caja existe y pertenece al negocio
        CajaRegistradora caja = cajaRepository.findByIdAndNegocioId(request.getCajaId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Caja registradora", request.getCajaId()));

        if (!caja.getEstaActivo()) {
            throw new OperacionInvalidaException("La caja está inactiva y no puede abrirse");
        }

        // Validar que no haya sesión abierta en esa caja
        long sesionesAbiertas = sesionRepository.countSesionesAbiertasPorCaja(negocioId, request.getCajaId());
        if (sesionesAbiertas > 0) {
            throw new OperacionInvalidaException("Ya existe una sesión abierta para esta caja. Debe cerrarla primero.");
        }

        // Validar usuario
        Usuario usuario = usuarioRepository.findByIdAndNegocioId(usuarioId, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuarioId));

        SesionCaja sesion = new SesionCaja();
        sesion.setNegocioId(negocioId);
        sesion.setCajaId(request.getCajaId());
        sesion.setUsuarioAperturaId(usuarioId);
        sesion.setMontoInicial(request.getMontoInicial() != null ? request.getMontoInicial() : BigDecimal.ZERO);
        sesion.setObservacionesApertura(request.getObservacionesApertura());
        sesion.setEstaAbierta(true);
        sesion.setAbiertaEn(LocalDateTime.now());

        sesionRepository.save(sesion);
        return convertirADTO(sesion);
    }

    /** Cerrar sesión de caja */
    @Transactional
    public SesionCajaDTO cerrarSesion(Long sesionId, CerrarSesionCajaRequest request, Long usuarioId, Long negocioId) {
        SesionCaja sesion = sesionRepository.findByIdAndNegocioId(sesionId, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión de caja", sesionId));

        if (!sesion.getEstaAbierta()) {
            throw new OperacionInvalidaException("La sesión ya está cerrada");
        }

        // Validar usuario
        usuarioRepository.findByIdAndNegocioId(usuarioId, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuarioId));

        // Calcular monto esperado (inicial + ventas + movimientos)
        BigDecimal totalVentas = ventaRepository.calcularTotalVentasSesion(negocioId, sesionId);
        BigDecimal ingresos = movimientoCajaRepository.calcularTotalPorTipo(negocioId, sesionId, "INGRESO");
        BigDecimal egresos = movimientoCajaRepository.calcularTotalPorTipo(negocioId, sesionId, "EGRESO");

        BigDecimal montoEsperado = sesion.getMontoInicial()
                .add(totalVentas)
                .add(ingresos)
                .subtract(egresos);

        // Calcular diferencia
        BigDecimal diferencia = request.getMontoFinal().subtract(montoEsperado);

        sesion.setUsuarioCierreId(usuarioId);
        sesion.setMontoFinal(request.getMontoFinal());
        sesion.setMontoEsperado(montoEsperado);
        sesion.setDiferencia(diferencia);
        sesion.setObservacionesCierre(request.getObservacionesCierre());
        sesion.setEstaAbierta(false);
        sesion.setCerradaEn(LocalDateTime.now());

        sesionRepository.save(sesion);
        return convertirADTO(sesion);
    }

    /** Eliminar sesión (soft delete) - solo si no tiene ventas */
    @Transactional
    public void eliminar(Long id, Long negocioId) {
        SesionCaja sesion = sesionRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión de caja", id));

        if (sesion.getEstaAbierta()) {
            throw new OperacionInvalidaException("No se puede eliminar una sesión abierta");
        }

        // Validar que no tenga ventas asociadas
        long ventasAsociadas = ventaRepository.findByNegocioIdAndSesionId(negocioId, id).size();
        if (ventasAsociadas > 0) {
            throw new OperacionInvalidaException("No se puede eliminar una sesión con ventas asociadas");
        }

        sesionRepository.delete(sesion);
    }

    // ============================================================
    // WRAPPERS PARA CONTROLADORES
    // ============================================================

    /** Listar sesiones por fecha */
    public List<SesionCajaDTO> listarPorFecha(Long negocioId, java.time.LocalDate fecha) {
        java.time.LocalDateTime inicio = fecha.atStartOfDay();
        java.time.LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return sesionRepository.findByNegocioId(negocioId).stream()
                .filter(s -> s.getAbiertaEn().isAfter(inicio) && s.getAbiertaEn().isBefore(fin))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Convertir entidad a DTO */
    private SesionCajaDTO convertirADTO(SesionCaja sesion) {
        SesionCajaDTO dto = new SesionCajaDTO();
        dto.setId(sesion.getId());
        dto.setNegocioId(sesion.getNegocioId());
        dto.setCajaId(sesion.getCajaId());
        if (sesion.getCaja() != null) {
            dto.setCajaNombre(sesion.getCaja().getNombreCaja());
        }
        dto.setUsuarioAperturaId(sesion.getUsuarioAperturaId());
        if (sesion.getUsuarioApertura() != null) {
            dto.setUsuarioAperturaNombre(
                    sesion.getUsuarioApertura().getNombres() + " " + sesion.getUsuarioApertura().getApellidos());
        }
        dto.setUsuarioCierreId(sesion.getUsuarioCierreId());
        if (sesion.getUsuarioCierre() != null) {
            dto.setUsuarioCierreNombre(
                    sesion.getUsuarioCierre().getNombres() + " " + sesion.getUsuarioCierre().getApellidos());
        }
        dto.setMontoInicial(sesion.getMontoInicial());
        dto.setMontoFinal(sesion.getMontoFinal());
        dto.setMontoEsperado(sesion.getMontoEsperado());
        dto.setDiferencia(sesion.getDiferencia());
        dto.setObservacionesApertura(sesion.getObservacionesApertura());
        dto.setObservacionesCierre(sesion.getObservacionesCierre());
        dto.setEstaAbierta(sesion.getEstaAbierta());
        dto.setAbiertaEn(sesion.getAbiertaEn());
        dto.setCerradaEn(sesion.getCerradaEn());
        dto.setCreadoEn(sesion.getCreadoEn());
        dto.setActualizadoEn(sesion.getActualizadoEn());
        return dto;
    }
}
