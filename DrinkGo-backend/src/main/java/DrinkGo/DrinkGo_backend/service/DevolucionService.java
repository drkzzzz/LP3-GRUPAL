package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.*;
import DrinkGo.DrinkGo_backend.entity.DetalleDevolucion;
import DrinkGo.DrinkGo_backend.entity.Devolucion;
import DrinkGo.DrinkGo_backend.repository.DetalleDevolucionRepository;
import DrinkGo.DrinkGo_backend.repository.DevolucionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de devoluciones y reembolsos - Bloque 11.
 * Implementa RF-FIN-008 al RF-FIN-012.
 */
@Service
public class DevolucionService {

    @Autowired
    private DevolucionRepository devolucionRepository;

    @Autowired
    private DetalleDevolucionRepository detalleDevolucionRepository;

    /**
     * RF-FIN-008: Registrar Solicitudes de Devolución.
     */
    @Transactional
    public DevolucionResponse crearDevolucion(CreateDevolucionRequest request) {
        // Validaciones
        if (request.getNegocioId() == null) {
            throw new RuntimeException("El negocioId es obligatorio");
        }
        if (request.getSedeId() == null) {
            throw new RuntimeException("El sedeId es obligatorio");
        }
        if (request.getTipoDevolucion() == null) {
            throw new RuntimeException("El tipo de devolución es obligatorio");
        }
        if (request.getCategoriaMotivo() == null) {
            throw new RuntimeException("La categoría del motivo es obligatoria");
        }
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new RuntimeException("Debe incluir al menos un detalle de devolución");
        }

        // Crear entidad Devolucion
        Devolucion devolucion = new Devolucion();
        devolucion.setNegocioId(request.getNegocioId());
        devolucion.setSedeId(request.getSedeId());
        devolucion.setVentaId(request.getVentaId());
        devolucion.setPedidoId(request.getPedidoId());
        devolucion.setClienteId(request.getClienteId());
        
        // Generar número de devolución único
        devolucion.setNumeroDevolucion(generarNumeroDevolucion(request.getNegocioId()));
        
        devolucion.setEstado(Devolucion.EstadoDevolucion.solicitada);
        devolucion.setTipoDevolucion(Devolucion.TipoDevolucion.valueOf(request.getTipoDevolucion()));
        devolucion.setCategoriaMotivo(Devolucion.CategoriaMotivo.valueOf(request.getCategoriaMotivo()));
        devolucion.setDetalleMotivo(request.getDetalleMotivo());
        
        if (request.getMetodoReembolso() != null) {
            devolucion.setMetodoReembolso(Devolucion.MetodoReembolso.valueOf(request.getMetodoReembolso()));
        }
        
        devolucion.setSubtotal(request.getSubtotal() != null ? request.getSubtotal() : BigDecimal.ZERO);
        devolucion.setMontoImpuesto(request.getMontoImpuesto() != null ? request.getMontoImpuesto() : BigDecimal.ZERO);
        devolucion.setTotal(request.getTotal() != null ? request.getTotal() : BigDecimal.ZERO);
        devolucion.setSolicitadoPor(request.getSolicitadoPor());
        devolucion.setNotas(request.getNotas());

        // Guardar devolución principal
        devolucion = devolucionRepository.save(devolucion);

        // Crear detalles
        for (DevolucionItemDTO itemDTO : request.getDetalles()) {
            DetalleDevolucion detalle = new DetalleDevolucion();
            detalle.setDevolucion(devolucion);
            detalle.setDetalleVentaId(itemDTO.getDetalleVentaId());
            detalle.setProductoId(itemDTO.getProductoId());
            detalle.setCantidad(itemDTO.getCantidad());
            detalle.setPrecioUnitario(itemDTO.getPrecioUnitario());
            detalle.setTotal(itemDTO.getTotal());
            detalle.setDevolverStock(itemDTO.getDevolverStock() != null ? itemDTO.getDevolverStock() : true);
            detalle.setNotas(itemDTO.getNotas());
            
            detalleDevolucionRepository.save(detalle);
            devolucion.addDetalle(detalle);
        }

        return toResponse(devolucion);
    }

    /**
     * Obtener todas las devoluciones (filtradas por negocio).
     */
    @Transactional(readOnly = true)
    public List<DevolucionResponse> obtenerTodas(Long negocioId) {
        List<Devolucion> devoluciones = devolucionRepository.findByNegocioId(negocioId);
        return devoluciones.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener devolución por ID.
     */
    @Transactional(readOnly = true)
    public DevolucionResponse obtenerPorId(Long id, Long negocioId) {
        Devolucion devolucion = devolucionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devolución no encontrada"));
        
        // Validar que pertenece al negocio
        if (!devolucion.getNegocioId().equals(negocioId)) {
            throw new RuntimeException("No tiene permisos para ver esta devolución");
        }
        
        return toResponse(devolucion);
    }

    /**
     * Actualizar devolución existente.
     */
    @Transactional
    public DevolucionResponse actualizarDevolucion(Long id, UpdateDevolucionRequest request, Long negocioId) {
        Devolucion devolucion = devolucionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devolución no encontrada"));
        
        // Validar que pertenece al negocio
        if (!devolucion.getNegocioId().equals(negocioId)) {
            throw new RuntimeException("No tiene permisos para modificar esta devolución");
        }
        
        // Solo se puede actualizar si está en estado 'solicitada'
        if (devolucion.getEstado() != Devolucion.EstadoDevolucion.solicitada) {
            throw new RuntimeException("Solo se pueden actualizar devoluciones en estado 'solicitada'");
        }

        // Actualizar campos permitidos
        if (request.getCategoriaMotivo() != null) {
            devolucion.setCategoriaMotivo(Devolucion.CategoriaMotivo.valueOf(request.getCategoriaMotivo()));
        }
        if (request.getDetalleMotivo() != null) {
            devolucion.setDetalleMotivo(request.getDetalleMotivo());
        }
        if (request.getMetodoReembolso() != null) {
            devolucion.setMetodoReembolso(Devolucion.MetodoReembolso.valueOf(request.getMetodoReembolso()));
        }
        if (request.getSubtotal() != null) {
            devolucion.setSubtotal(request.getSubtotal());
        }
        if (request.getMontoImpuesto() != null) {
            devolucion.setMontoImpuesto(request.getMontoImpuesto());
        }
        if (request.getTotal() != null) {
            devolucion.setTotal(request.getTotal());
        }
        if (request.getNotas() != null) {
            devolucion.setNotas(request.getNotas());
        }

        // Actualizar detalles si se proporcionan
        if (request.getDetalles() != null && !request.getDetalles().isEmpty()) {
            // Eliminar detalles existentes
            List<DetalleDevolucion> detallesExistentes = detalleDevolucionRepository.findByDevolucionId(id);
            detalleDevolucionRepository.deleteAll(detallesExistentes);
            
            // Crear nuevos detalles
            devolucion.getDetalles().clear();
            for (DevolucionItemDTO itemDTO : request.getDetalles()) {
                DetalleDevolucion detalle = new DetalleDevolucion();
                detalle.setDevolucion(devolucion);
                detalle.setDetalleVentaId(itemDTO.getDetalleVentaId());
                detalle.setProductoId(itemDTO.getProductoId());
                detalle.setCantidad(itemDTO.getCantidad());
                detalle.setPrecioUnitario(itemDTO.getPrecioUnitario());
                detalle.setTotal(itemDTO.getTotal());
                detalle.setDevolverStock(itemDTO.getDevolverStock() != null ? itemDTO.getDevolverStock() : true);
                detalle.setNotas(itemDTO.getNotas());
                
                detalleDevolucionRepository.save(detalle);
                devolucion.addDetalle(detalle);
            }
        }

        devolucion = devolucionRepository.save(devolucion);
        return toResponse(devolucion);
    }

    /**
     * RF-FIN-009: Aprobar devolución.
     */
    @Transactional
    public DevolucionResponse aprobarDevolucion(Long id, AprobarDevolucionRequest request, Long negocioId) {
        Devolucion devolucion = devolucionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devolución no encontrada"));
        
        if (!devolucion.getNegocioId().equals(negocioId)) {
            throw new RuntimeException("No tiene permisos para aprobar esta devolución");
        }
        
        // Validar estado actual
        if (devolucion.getEstado() != Devolucion.EstadoDevolucion.solicitada) {
            throw new RuntimeException("Solo se pueden aprobar devoluciones en estado 'solicitada'");
        }

        // Cambiar estado a aprobada
        devolucion.setEstado(Devolucion.EstadoDevolucion.aprobada);
        devolucion.setAprobadoEn(LocalDateTime.now());
        devolucion.setAprobadoPor(request.getAprobadoPor());
        
        if (request.getNotas() != null) {
            String notasActuales = devolucion.getNotas() != null ? devolucion.getNotas() + "\n" : "";
            devolucion.setNotas(notasActuales + "APROBACIÓN: " + request.getNotas());
        }

        devolucion = devolucionRepository.save(devolucion);
        return toResponse(devolucion);
    }

    /**
     * RF-FIN-009: Rechazar devolución.
     */
    @Transactional
    public DevolucionResponse rechazarDevolucion(Long id, AprobarDevolucionRequest request, Long negocioId) {
        Devolucion devolucion = devolucionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devolución no encontrada"));
        
        if (!devolucion.getNegocioId().equals(negocioId)) {
            throw new RuntimeException("No tiene permisos para rechazar esta devolución");
        }
        
        // Validar estado actual
        if (devolucion.getEstado() != Devolucion.EstadoDevolucion.solicitada) {
            throw new RuntimeException("Solo se pueden rechazar devoluciones en estado 'solicitada'");
        }

        // Cambiar estado a rechazada
        devolucion.setEstado(Devolucion.EstadoDevolucion.rechazada);
        devolucion.setRechazadoEn(LocalDateTime.now());
        
        // Guardar razón del rechazo
        if (request.getNotas() != null) {
            devolucion.setRazonRechazo(request.getNotas());
            String notasActuales = devolucion.getNotas() != null ? devolucion.getNotas() + "\n" : "";
            devolucion.setNotas(notasActuales + "RECHAZO: " + request.getNotas());
        }

        devolucion = devolucionRepository.save(devolucion);
        return toResponse(devolucion);
    }

    /**
     * RF-FIN-010, RF-FIN-011, RF-FIN-012: Completar devolución y procesar reembolso.
     * Incluye reintegración de stock si aplica.
     */
    @Transactional
    public DevolucionResponse completarDevolucion(Long id, Long procesadoPor, Long negocioId) {
        Devolucion devolucion = devolucionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devolución no encontrada"));
        
        if (!devolucion.getNegocioId().equals(negocioId)) {
            throw new RuntimeException("No tiene permisos para completar esta devolución");
        }
        
        // Validar estado actual (debe estar aprobada)
        if (devolucion.getEstado() != Devolucion.EstadoDevolucion.aprobada) {
            throw new RuntimeException("Solo se pueden completar devoluciones en estado 'aprobada'");
        }

        // Cambiar estado a completada
        devolucion.setEstado(Devolucion.EstadoDevolucion.completada);
        devolucion.setCompletadoEn(LocalDateTime.now());
        devolucion.setProcesadoPor(procesadoPor);

        // TODO: RF-FIN-012 - Reintegrar productos al inventario
        // Este proceso debería:
        // 1. Iterar sobre los detalles de la devolución
        // 2. Para cada detalle donde devolver_stock = true
        // 3. Actualizar el stock del producto/lote correspondiente
        // 4. Registrar el movimiento de inventario
        // NOTA: Se implementará cuando esté disponible el servicio de Inventario

        // TODO: RF-FIN-010 - Generar ajustes financieros
        // Este proceso debería crear registros en el módulo financiero

        // TODO: RF-FIN-011 - Procesar reembolso
        // Este proceso debería registrar el reembolso según el método seleccionado

        devolucion = devolucionRepository.save(devolucion);
        return toResponse(devolucion);
    }

    /**
     * Eliminar/desactivar devolución (solo si está en estado solicitada).
     */
    @Transactional
    public String eliminarDevolucion(Long id, Long negocioId) {
        Devolucion devolucion = devolucionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devolución no encontrada"));
        
        if (!devolucion.getNegocioId().equals(negocioId)) {
            throw new RuntimeException("No tiene permisos para eliminar esta devolución");
        }
        
        // Solo se puede eliminar si está en estado 'solicitada'
        if (devolucion.getEstado() != Devolucion.EstadoDevolucion.solicitada) {
            throw new RuntimeException("Solo se pueden eliminar devoluciones en estado 'solicitada'");
        }

        devolucionRepository.delete(devolucion);
        return "Devolución eliminada correctamente";
    }

    // --- Métodos auxiliares ---

    /**
     * Genera un número único de devolución.
     */
    private String generarNumeroDevolucion(Long negocioId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "DEV-" + negocioId + "-" + timestamp;
    }

    /**
     * Convierte entidad a Response DTO.
     */
    private DevolucionResponse toResponse(Devolucion devolucion) {
        DevolucionResponse response = new DevolucionResponse();
        response.setId(devolucion.getId());
        response.setNegocioId(devolucion.getNegocioId());
        response.setSedeId(devolucion.getSedeId());
        response.setVentaId(devolucion.getVentaId());
        response.setPedidoId(devolucion.getPedidoId());
        response.setClienteId(devolucion.getClienteId());
        response.setNumeroDevolucion(devolucion.getNumeroDevolucion());
        response.setEstado(devolucion.getEstado().name());
        response.setTipoDevolucion(devolucion.getTipoDevolucion().name());
        response.setCategoriaMotivo(devolucion.getCategoriaMotivo() != null ? 
                devolucion.getCategoriaMotivo().name() : null);
        response.setDetalleMotivo(devolucion.getDetalleMotivo());
        response.setMetodoReembolso(devolucion.getMetodoReembolso() != null ? 
                devolucion.getMetodoReembolso().name() : null);
        response.setSubtotal(devolucion.getSubtotal());
        response.setMontoImpuesto(devolucion.getMontoImpuesto());
        response.setTotal(devolucion.getTotal());
        response.setNotas(devolucion.getNotas());
        response.setSolicitadoEn(devolucion.getSolicitadoEn());
        response.setAprobadoEn(devolucion.getAprobadoEn());
        response.setCompletadoEn(devolucion.getCompletadoEn());
        response.setRechazadoEn(devolucion.getRechazadoEn());
        response.setSolicitadoPor(devolucion.getSolicitadoPor());
        response.setProcesadoPor(devolucion.getProcesadoPor());
        response.setAprobadoPor(devolucion.getAprobadoPor());
        response.setRazonRechazo(devolucion.getRazonRechazo());
        response.setCreadoEn(devolucion.getCreadoEn());
        response.setActualizadoEn(devolucion.getActualizadoEn());

        // Convertir detalles
        List<DevolucionItemDTO> detallesDTO = new ArrayList<>();
        for (DetalleDevolucion detalle : devolucion.getDetalles()) {
            DevolucionItemDTO itemDTO = new DevolucionItemDTO();
            itemDTO.setId(detalle.getId());
            itemDTO.setDetalleVentaId(detalle.getDetalleVentaId());
            itemDTO.setProductoId(detalle.getProductoId());
            itemDTO.setCantidad(detalle.getCantidad());
            itemDTO.setPrecioUnitario(detalle.getPrecioUnitario());
            itemDTO.setTotal(detalle.getTotal());
            itemDTO.setDevolverStock(detalle.getDevolverStock());
            itemDTO.setNotas(detalle.getNotas());
            detallesDTO.add(itemDTO);
        }
        response.setDetalles(detallesDTO);

        return response;
    }
}
