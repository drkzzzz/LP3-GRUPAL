 package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.LoteFifoDTO;
import DrinkGo.DrinkGo_backend.dto.ProductoProximoVencerDTO;
import DrinkGo.DrinkGo_backend.repository.DatabaseFunctionsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * Servicio que expone las funciones PL/pgSQL de V2__database_functions.sql
 * 
 * Funciones implementadas:
 * - seleccionar_lotes_fifo: Selección FIFO de lotes para descuento
 * - descontar_inventario_fifo: Descuento automático usando FIFO
 * - obtener_productos_proximos_vencer: Alertas de vencimiento
 * - descontar_combo_inventario: Descuento atómico de combos
 * - validar_horario_venta_alcohol: Validación de horarios
 * - marcar_lotes_vencidos: Job para lotes vencidos
 */
@Service
public class DatabaseFunctionsService {
    
    private final DatabaseFunctionsRepository functionsRepository;
    
    public DatabaseFunctionsService(DatabaseFunctionsRepository functionsRepository) {
        this.functionsRepository = functionsRepository;
    }
    
    // =========================================================================
    // FIFO - Selección de lotes
    // =========================================================================
    
    /**
     * Selecciona lotes siguiendo FIFO para una cantidad requerida.
     * Prioriza: 1) Fecha vencimiento más cercana, 2) Fecha entrada más antigua
     * 
     * @param productoId ID del producto
     * @param sedeId ID de la sede
     * @param cantidadRequerida Cantidad a descontar
     * @return Lista de lotes con cantidades a descontar
     */
    @Transactional(readOnly = true)
    public List<LoteFifoDTO> seleccionarLotesFifo(Long productoId, Long sedeId, BigDecimal cantidadRequerida) {
        return functionsRepository.seleccionarLotesFifo(productoId, sedeId, cantidadRequerida);
    }
    
    // =========================================================================
    // FIFO - Descuento de inventario
    // =========================================================================
    
    /**
     * Descuenta inventario usando FIFO automático.
     * Registra movimientos de inventario para trazabilidad.
     * 
     * @param productoId ID del producto
     * @param sedeId ID de la sede
     * @param cantidad Cantidad a descontar
     * @param referenciaTipo Tipo: 'venta', 'pedido', 'merma', 'ajuste'
     * @param referenciaId ID de la referencia (venta_id, pedido_id, etc.)
     */
    @Transactional
    public void descontarInventarioFifo(Long productoId, Long sedeId, BigDecimal cantidad,
                                         String referenciaTipo, Long referenciaId) {
        functionsRepository.descontarInventarioFifo(productoId, sedeId, cantidad, referenciaTipo, referenciaId);
    }
    
    /**
     * Descuenta inventario por venta
     */
    @Transactional
    public void descontarPorVenta(Long productoId, Long sedeId, BigDecimal cantidad, Long ventaId) {
        descontarInventarioFifo(productoId, sedeId, cantidad, "venta", ventaId);
    }
    
    /**
     * Descuenta inventario por pedido
     */
    @Transactional
    public void descontarPorPedido(Long productoId, Long sedeId, BigDecimal cantidad, Long pedidoId) {
        descontarInventarioFifo(productoId, sedeId, cantidad, "pedido", pedidoId);
    }
    
    /**
     * Registra merma de inventario
     */
    @Transactional
    public void registrarMerma(Long productoId, Long sedeId, BigDecimal cantidad, Long mermaId) {
        descontarInventarioFifo(productoId, sedeId, cantidad, "merma", mermaId);
    }
    
    // =========================================================================
    // Alertas de vencimiento
    // =========================================================================
    
    /**
     * Obtiene productos próximos a vencer para un tenant.
     * 
     * @param tenantId ID del negocio
     * @param sedeId ID de sede (null para todas)
     * @param diasAlerta Días de anticipación (default 30)
     * @return Lista de productos con nivel de urgencia
     */
    @Transactional(readOnly = true)
    public List<ProductoProximoVencerDTO> obtenerProductosProximosVencer(Long tenantId, Long sedeId, int diasAlerta) {
        return functionsRepository.obtenerProductosProximosVencer(tenantId, sedeId, diasAlerta);
    }
    
    /**
     * Obtiene productos próximos a vencer en todas las sedes (30 días)
     */
    @Transactional(readOnly = true)
    public List<ProductoProximoVencerDTO> obtenerProductosProximosVencer(Long tenantId) {
        return obtenerProductosProximosVencer(tenantId, null, 30);
    }
    
    /**
     * Obtiene productos críticos (7 días o menos)
     */
    @Transactional(readOnly = true)
    public List<ProductoProximoVencerDTO> obtenerProductosCriticos(Long tenantId) {
        return obtenerProductosProximosVencer(tenantId, null, 7);
    }
    
    // =========================================================================
    // Combos y packs
    // =========================================================================
    
    /**
     * Descuenta todos los componentes de un combo/pack atómicamente.
     * 
     * @param comboId ID del combo
     * @param sedeId ID de la sede
     * @param cantidadCombos Cantidad de combos vendidos
     * @param referenciaTipo Tipo de referencia
     * @param referenciaId ID de referencia
     */
    @Transactional
    public void descontarComboInventario(Long comboId, Long sedeId, int cantidadCombos,
                                          String referenciaTipo, Long referenciaId) {
        functionsRepository.descontarComboInventario(comboId, sedeId, cantidadCombos, referenciaTipo, referenciaId);
    }
    
    /**
     * Descuenta combo por venta
     */
    @Transactional
    public void descontarComboPorVenta(Long comboId, Long sedeId, int cantidadCombos, Long ventaId) {
        descontarComboInventario(comboId, sedeId, cantidadCombos, "venta", ventaId);
    }
    
    // =========================================================================
    // Validación de horarios de venta de alcohol
    // =========================================================================
    
    /**
     * Valida si se puede vender alcohol en este momento según configuración de sede.
     * 
     * @param sedeId ID de la sede
     * @return true si la venta está permitida
     */
    @Transactional(readOnly = true)
    public boolean validarHorarioVentaAlcohol(Long sedeId) {
        return functionsRepository.validarHorarioVentaAlcohol(sedeId);
    }
    
    /**
     * Valida para una hora específica
     */
    @Transactional(readOnly = true)
    public boolean validarHorarioVentaAlcohol(Long sedeId, LocalTime hora) {
        return functionsRepository.validarHorarioVentaAlcohol(sedeId, hora);
    }
    
    // =========================================================================
    // Jobs programados
    // =========================================================================
    
    /**
     * Marca automáticamente como vencidos los lotes con fecha pasada.
     * Diseñado para ejecutarse como job diario.
     * 
     * @return Cantidad de lotes marcados como vencidos
     */
    @Transactional
    public int marcarLotesVencidos() {
        return functionsRepository.marcarLotesVencidos();
    }
}
