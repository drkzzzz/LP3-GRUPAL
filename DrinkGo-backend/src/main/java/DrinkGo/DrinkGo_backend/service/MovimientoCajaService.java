package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.MovimientoCajaDTO;
import DrinkGo.DrinkGo_backend.dto.MovimientoCajaRequest;
import DrinkGo.DrinkGo_backend.entity.MovimientoCaja;
import DrinkGo.DrinkGo_backend.entity.SesionCaja;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.repository.MovimientoCajaRepository;
import DrinkGo.DrinkGo_backend.repository.SesionCajaRepository;
import DrinkGo.DrinkGo_backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de movimientos de caja (ingresos/egresos/ajustes).
 * Bloque 8 - Ventas, POS y Cajas.
 */
@Service
public class MovimientoCajaService {

    private final MovimientoCajaRepository movimientoRepository;
    private final SesionCajaRepository sesionRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimientoCajaService(MovimientoCajaRepository movimientoRepository,
            SesionCajaRepository sesionRepository,
            UsuarioRepository usuarioRepository) {
        this.movimientoRepository = movimientoRepository;
        this.sesionRepository = sesionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /** Listar movimientos del negocio */
    public List<MovimientoCajaDTO> listar(Long negocioId) {
        return movimientoRepository.findByNegocioId(negocioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar movimientos de una sesión */
    public List<MovimientoCajaDTO> listarPorSesion(Long sesionId, Long negocioId) {
        return movimientoRepository.findByNegocioIdAndSesionId(negocioId, sesionId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar movimientos por tipo */
    public List<MovimientoCajaDTO> listarPorTipo(String tipoMovimiento, Long negocioId) {
        return movimientoRepository.findByNegocioIdAndTipoMovimiento(negocioId, tipoMovimiento).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Obtener movimiento por ID */
    public MovimientoCajaDTO obtenerPorId(Long id, Long negocioId) {
        MovimientoCaja movimiento = movimientoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento de caja", id));
        return convertirADTO(movimiento);
    }

    /** Crear movimiento de caja */
    @Transactional
    public MovimientoCajaDTO crear(Long negocioId, Long usuarioId, MovimientoCajaRequest request) {
        // Validar sesión
        SesionCaja sesion = sesionRepository.findByIdAndNegocioId(request.getSesionId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión de caja", request.getSesionId()));

        if (!sesion.getEstaAbierta()) {
            throw new OperacionInvalidaException("La sesión de caja está cerrada. No se pueden registrar movimientos.");
        }

        // Validar usuario
        usuarioRepository.findByIdAndNegocioId(usuarioId, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuarioId));

        // Validar tipo de movimiento
        if (!request.getTipoMovimiento().matches("INGRESO|EGRESO|AJUSTE")) {
            throw new OperacionInvalidaException("Tipo de movimiento inválido. Use: INGRESO, EGRESO o AJUSTE");
        }

        // Validar monto positivo
        if (request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperacionInvalidaException("El monto debe ser mayor a cero");
        }

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setNegocioId(negocioId);
        movimiento.setSesionId(request.getSesionId());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setMonto(request.getMonto());
        movimiento.setConcepto(request.getConcepto());
        movimiento.setRealizadoPorUsuarioId(usuarioId);

        movimientoRepository.save(movimiento);
        return convertirADTO(movimiento);
    }

    /** Eliminar movimiento (soft delete) - solo de sesiones abiertas */
    @Transactional
    public void eliminar(Long id, Long negocioId) {
        MovimientoCaja movimiento = movimientoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento de caja", id));

        SesionCaja sesion = sesionRepository.findByIdAndNegocioId(movimiento.getSesionId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión de caja", movimiento.getSesionId()));

        if (!sesion.getEstaAbierta()) {
            throw new OperacionInvalidaException("No se pueden eliminar movimientos de sesiones cerradas");
        }

        movimientoRepository.delete(movimiento);
    }

    // ============================================================
    // WRAPPERS PARA CONTROLADORES
    // ============================================================

    /** Wrapper para obtener con orden alternativo de parámetros */
    public MovimientoCajaDTO obtener(Long negocioId, Long id) {
        return obtenerPorId(id, negocioId);
    }

    /** Listar movimientos por sesión y tipo */
    public List<MovimientoCajaDTO> listarPorSesionYTipo(Long negocioId, Long sesionId, String tipo) {
        return movimientoRepository.findByNegocioIdAndSesionId(negocioId, sesionId).stream()
                .filter(m -> m.getTipoMovimiento().equals(tipo))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar movimientos por rango de fechas */
    public List<MovimientoCajaDTO> listarPorRango(Long negocioId, java.time.LocalDateTime fechaInicio,
            java.time.LocalDateTime fechaFin) {
        return movimientoRepository.findByNegocioId(negocioId).stream()
                .filter(m -> !m.getCreadoEn().isBefore(fechaInicio) && !m.getCreadoEn().isAfter(fechaFin))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Actualizar movimiento */
    @Transactional
    public MovimientoCajaDTO actualizar(Long negocioId, Long id, MovimientoCajaRequest request) {
        MovimientoCaja movimiento = movimientoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento de caja", id));

        SesionCaja sesion = sesionRepository.findByIdAndNegocioId(movimiento.getSesionId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión de caja", movimiento.getSesionId()));

        if (!sesion.getEstaAbierta()) {
            throw new OperacionInvalidaException("No se pueden actualizar movimientos de sesiones cerradas");
        }

        if (request.getTipoMovimiento() != null) {
            if (!request.getTipoMovimiento().matches("INGRESO|EGRESO|AJUSTE")) {
                throw new OperacionInvalidaException("Tipo de movimiento inválido");
            }
            movimiento.setTipoMovimiento(request.getTipoMovimiento());
        }
        if (request.getMonto() != null) {
            if (request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new OperacionInvalidaException("El monto debe ser mayor a cero");
            }
            movimiento.setMonto(request.getMonto());
        }
        if (request.getConcepto() != null) {
            movimiento.setConcepto(request.getConcepto());
        }

        movimientoRepository.save(movimiento);
        return convertirADTO(movimiento);
    }

    /** Convertir entidad a DTO */
    private MovimientoCajaDTO convertirADTO(MovimientoCaja movimiento) {
        MovimientoCajaDTO dto = new MovimientoCajaDTO();
        dto.setId(movimiento.getId());
        dto.setNegocioId(movimiento.getNegocioId());
        dto.setSesionId(movimiento.getSesionId());
        dto.setTipoMovimiento(movimiento.getTipoMovimiento());
        dto.setMonto(movimiento.getMonto());
        dto.setConcepto(movimiento.getConcepto());
        dto.setRealizadoPorUsuarioId(movimiento.getRealizadoPorUsuarioId());
        if (movimiento.getRealizadoPorUsuario() != null) {
            dto.setRealizadoPorUsuarioNombre(
                    movimiento.getRealizadoPorUsuario().getNombres() + " " +
                            movimiento.getRealizadoPorUsuario().getApellidos());
        }
        dto.setCreadoEn(movimiento.getCreadoEn());
        return dto;
    }
}
