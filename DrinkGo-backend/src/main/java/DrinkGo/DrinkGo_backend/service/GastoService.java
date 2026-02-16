package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.*;
import DrinkGo.DrinkGo_backend.entity.CategoriaGasto;
import DrinkGo.DrinkGo_backend.entity.Gasto;
import DrinkGo.DrinkGo_backend.repository.CategoriaGastoRepository;
import DrinkGo.DrinkGo_backend.repository.GastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gestión de gastos e ingresos.
 * RF-FIN-013: Registrar Gastos y Egresos
 * RF-FIN-014: Categorizar Gastos
 * RF-FIN-015: Gestionar Comprobantes de Gasto
 */
@Service
public class GastoService {

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private CategoriaGastoRepository categoriaGastoRepository;

    /**
     * Crear un nuevo gasto.
     */
    @Transactional
    public GastoResponse crearGasto(CreateGastoRequest request) {
        // Validaciones
        if (request.getNegocioId() == null) {
            throw new RuntimeException("El negocioId es obligatorio");
        }
        if (request.getSedeId() == null) {
            throw new RuntimeException("El sedeId es obligatorio");
        }
        if (request.getCategoriaId() == null) {
            throw new RuntimeException("El categoriaId es obligatorio");
        }
        if (request.getDescripcion() == null || request.getDescripcion().trim().isEmpty()) {
            throw new RuntimeException("La descripción es obligatoria");
        }
        if (request.getTotal() == null || request.getTotal().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El total debe ser mayor a cero");
        }
        if (request.getRegistradoPor() == null) {
            throw new RuntimeException("El registradoPor es obligatorio");
        }
        if (request.getFechaGasto() == null) {
            throw new RuntimeException("La fechaGasto es obligatoria");
        }

        // Verificar que la categoría existe
        CategoriaGasto categoria = categoriaGastoRepository.findById(request.getCategoriaId())
            .orElseThrow(() -> new RuntimeException("Categoría de gasto no encontrada"));

        // Crear entidad
        Gasto gasto = new Gasto();
        gasto.setNegocioId(request.getNegocioId());
        gasto.setSedeId(request.getSedeId());
        gasto.setCategoriaId(request.getCategoriaId());
        gasto.setProveedorId(request.getProveedorId());
        gasto.setDescripcion(request.getDescripcion());
        gasto.setMonto(request.getMonto() != null ? request.getMonto() : request.getTotal());
        gasto.setMontoImpuesto(request.getMontoImpuesto() != null ? request.getMontoImpuesto() : java.math.BigDecimal.ZERO);
        gasto.setTotal(request.getTotal());
        gasto.setMoneda(request.getMoneda() != null ? request.getMoneda() : "PEN");
        gasto.setFechaGasto(request.getFechaGasto());
        gasto.setReferenciaPago(request.getReferenciaPago());
        gasto.setUrlComprobante(request.getUrlComprobante());
        gasto.setEstado(Gasto.EstadoGasto.pendiente);
        gasto.setEsRecurrente(request.getEsRecurrente() != null ? request.getEsRecurrente() : false);
        gasto.setRegistradoPor(request.getRegistradoPor());
        gasto.setNotas(request.getNotas());

        // Generar número de gasto único
        gasto.setNumeroGasto(generarNumeroGasto(request.getNegocioId()));

        // Convertir método de pago
        if (request.getMetodoPago() != null) {
            try {
                gasto.setMetodoPago(Gasto.MetodoPago.valueOf(request.getMetodoPago()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Método de pago inválido: " + request.getMetodoPago());
            }
        }

        // Convertir período de recurrencia si existe
        if (request.getPeriodoRecurrencia() != null && request.getEsRecurrente()) {
            try {
                gasto.setPeriodoRecurrencia(Gasto.PeriodoRecurrencia.valueOf(request.getPeriodoRecurrencia()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Período de recurrencia inválido: " + request.getPeriodoRecurrencia());
            }
        }

        // Guardar
        Gasto gastoGuardado = gastoRepository.save(gasto);

        // Convertir a response
        return convertirAResponse(gastoGuardado, categoria.getNombre());
    }

    /**
     * Obtener todos los gastos de un negocio.
     */
    public List<GastoResponse> obtenerTodos(Long negocioId, Long sedeId) {
        List<Gasto> gastos;
        
        if (sedeId != null) {
            gastos = gastoRepository.findByNegocioIdAndSedeId(negocioId, sedeId);
        } else {
            gastos = gastoRepository.findByNegocioId(negocioId);
        }

        return gastos.stream()
            .map(gasto -> {
                String categoriaNombre = obtenerNombreCategoria(gasto.getCategoriaId());
                return convertirAResponse(gasto, categoriaNombre);
            })
            .collect(Collectors.toList());
    }

    /**
     * Obtener un gasto por ID.
     */
    public GastoResponse obtenerPorId(Long id, Long negocioId) {
        Gasto gasto = gastoRepository.findByIdAndNegocioId(id, negocioId)
            .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        String categoriaNombre = obtenerNombreCategoria(gasto.getCategoriaId());
        return convertirAResponse(gasto, categoriaNombre);
    }

    /**
     * Actualizar un gasto (solo si está en estado pendiente).
     */
    @Transactional
    public GastoResponse actualizarGasto(Long id, Long negocioId, UpdateGastoRequest request) {
        Gasto gasto = gastoRepository.findByIdAndNegocioId(id, negocioId)
            .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        // Solo se puede actualizar si está pendiente
        if (gasto.getEstado() != Gasto.EstadoGasto.pendiente) {
            throw new RuntimeException("Solo se pueden actualizar gastos en estado 'pendiente'");
        }

        // Actualizar campos
        if (request.getCategoriaId() != null) {
            // Verificar que la categoría existe
            categoriaGastoRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría de gasto no encontrada"));
            gasto.setCategoriaId(request.getCategoriaId());
        }
        if (request.getProveedorId() != null) {
            gasto.setProveedorId(request.getProveedorId());
        }
        if (request.getDescripcion() != null) {
            gasto.setDescripcion(request.getDescripcion());
        }
        if (request.getMonto() != null) {
            gasto.setMonto(request.getMonto());
        }
        if (request.getMontoImpuesto() != null) {
            gasto.setMontoImpuesto(request.getMontoImpuesto());
        }
        if (request.getTotal() != null) {
            gasto.setTotal(request.getTotal());
        }
        if (request.getFechaGasto() != null) {
            gasto.setFechaGasto(request.getFechaGasto());
        }
        if (request.getMetodoPago() != null) {
            try {
                gasto.setMetodoPago(Gasto.MetodoPago.valueOf(request.getMetodoPago()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Método de pago inválido: " + request.getMetodoPago());
            }
        }
        if (request.getReferenciaPago() != null) {
            gasto.setReferenciaPago(request.getReferenciaPago());
        }
        if (request.getUrlComprobante() != null) {
            gasto.setUrlComprobante(request.getUrlComprobante());
        }
        if (request.getEsRecurrente() != null) {
            gasto.setEsRecurrente(request.getEsRecurrente());
        }
        if (request.getPeriodoRecurrencia() != null) {
            try {
                gasto.setPeriodoRecurrencia(Gasto.PeriodoRecurrencia.valueOf(request.getPeriodoRecurrencia()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Período de recurrencia inválido: " + request.getPeriodoRecurrencia());
            }
        }
        if (request.getNotas() != null) {
            gasto.setNotas(request.getNotas());
        }

        Gasto gastoActualizado = gastoRepository.save(gasto);
        String categoriaNombre = obtenerNombreCategoria(gastoActualizado.getCategoriaId());
        return convertirAResponse(gastoActualizado, categoriaNombre);
    }

    /**
     * Aprobar un gasto.
     */
    @Transactional
    public GastoResponse aprobarGasto(Long id, Long negocioId, AprobarGastoRequest request) {
        Gasto gasto = gastoRepository.findByIdAndNegocioId(id, negocioId)
            .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        // Solo se puede aprobar si está pendiente
        if (gasto.getEstado() != Gasto.EstadoGasto.pendiente) {
            throw new RuntimeException("Solo se pueden aprobar gastos en estado 'pendiente'");
        }

        gasto.setEstado(Gasto.EstadoGasto.aprobado);
        gasto.setAprobadoPor(request.getAprobadoPor());
        
        if (request.getNotas() != null) {
            String notasActuales = gasto.getNotas() != null ? gasto.getNotas() + "\n" : "";
            gasto.setNotas(notasActuales + "APROBACIÓN: " + request.getNotas());
        }

        Gasto gastoAprobado = gastoRepository.save(gasto);
        String categoriaNombre = obtenerNombreCategoria(gastoAprobado.getCategoriaId());
        return convertirAResponse(gastoAprobado, categoriaNombre);
    }

    /**
     * Rechazar un gasto.
     */
    @Transactional
    public GastoResponse rechazarGasto(Long id, Long negocioId, AprobarGastoRequest request) {
        Gasto gasto = gastoRepository.findByIdAndNegocioId(id, negocioId)
            .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        // Solo se puede rechazar si está pendiente
        if (gasto.getEstado() != Gasto.EstadoGasto.pendiente) {
            throw new RuntimeException("Solo se pueden rechazar gastos en estado 'pendiente'");
        }

        gasto.setEstado(Gasto.EstadoGasto.rechazado);
        gasto.setAprobadoPor(request.getAprobadoPor());
        
        if (request.getNotas() != null) {
            String notasActuales = gasto.getNotas() != null ? gasto.getNotas() + "\n" : "";
            gasto.setNotas(notasActuales + "RECHAZO: " + request.getNotas());
        }

        Gasto gastoRechazado = gastoRepository.save(gasto);
        String categoriaNombre = obtenerNombreCategoria(gastoRechazado.getCategoriaId());
        return convertirAResponse(gastoRechazado, categoriaNombre);
    }

    /**
     * Marcar gasto como pagado.
     */
    @Transactional
    public GastoResponse marcarComoPagado(Long id, Long negocioId) {
        Gasto gasto = gastoRepository.findByIdAndNegocioId(id, negocioId)
            .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        // Solo se puede marcar como pagado si está aprobado
        if (gasto.getEstado() != Gasto.EstadoGasto.aprobado) {
            throw new RuntimeException("Solo se pueden marcar como pagados los gastos en estado 'aprobado'");
        }

        gasto.setEstado(Gasto.EstadoGasto.pagado);
        
        Gasto gastoPagado = gastoRepository.save(gasto);
        String categoriaNombre = obtenerNombreCategoria(gastoPagado.getCategoriaId());
        return convertirAResponse(gastoPagado, categoriaNombre);
    }

    /**
     * Anular un gasto.
     */
    @Transactional
    public GastoResponse anularGasto(Long id, Long negocioId) {
        Gasto gasto = gastoRepository.findByIdAndNegocioId(id, negocioId)
            .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        // No se puede anular si ya está pagado
        if (gasto.getEstado() == Gasto.EstadoGasto.pagado) {
            throw new RuntimeException("No se puede anular un gasto que ya está pagado");
        }

        gasto.setEstado(Gasto.EstadoGasto.anulado);
        
        Gasto gastoAnulado = gastoRepository.save(gasto);
        String categoriaNombre = obtenerNombreCategoria(gastoAnulado.getCategoriaId());
        return convertirAResponse(gastoAnulado, categoriaNombre);
    }

    /**
     * Eliminar un gasto (solo si está pendiente).
     */
    @Transactional
    public void eliminarGasto(Long id, Long negocioId) {
        Gasto gasto = gastoRepository.findByIdAndNegocioId(id, negocioId)
            .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        // Solo se puede eliminar si está pendiente
        if (gasto.getEstado() != Gasto.EstadoGasto.pendiente) {
            throw new RuntimeException("Solo se pueden eliminar gastos en estado 'pendiente'");
        }

        gastoRepository.delete(gasto);
    }

    // --- Métodos auxiliares ---

    private String generarNumeroGasto(Long negocioId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "GAS-" + negocioId + "-" + timestamp;
    }

    private String obtenerNombreCategoria(Long categoriaId) {
        return categoriaGastoRepository.findById(categoriaId)
            .map(CategoriaGasto::getNombre)
            .orElse("Sin categoría");
    }

    private GastoResponse convertirAResponse(Gasto gasto, String categoriaNombre) {
        GastoResponse response = new GastoResponse();
        response.setId(gasto.getId());
        response.setNegocioId(gasto.getNegocioId());
        response.setSedeId(gasto.getSedeId());
        response.setNumeroGasto(gasto.getNumeroGasto());
        response.setCategoriaId(gasto.getCategoriaId());
        response.setCategoriaNombre(categoriaNombre);
        response.setProveedorId(gasto.getProveedorId());
        response.setDescripcion(gasto.getDescripcion());
        response.setMonto(gasto.getMonto());
        response.setMontoImpuesto(gasto.getMontoImpuesto());
        response.setTotal(gasto.getTotal());
        response.setMoneda(gasto.getMoneda());
        response.setFechaGasto(gasto.getFechaGasto());
        response.setMetodoPago(gasto.getMetodoPago() != null ? gasto.getMetodoPago().name() : null);
        response.setReferenciaPago(gasto.getReferenciaPago());
        response.setUrlComprobante(gasto.getUrlComprobante());
        response.setEstado(gasto.getEstado() != null ? gasto.getEstado().name() : null);
        response.setEsRecurrente(gasto.getEsRecurrente());
        response.setPeriodoRecurrencia(gasto.getPeriodoRecurrencia() != null ? gasto.getPeriodoRecurrencia().name() : null);
        response.setAprobadoPor(gasto.getAprobadoPor());
        response.setRegistradoPor(gasto.getRegistradoPor());
        response.setNotas(gasto.getNotas());
        response.setCreadoEn(gasto.getCreadoEn());
        response.setActualizadoEn(gasto.getActualizadoEn());
        return response;
    }
}
