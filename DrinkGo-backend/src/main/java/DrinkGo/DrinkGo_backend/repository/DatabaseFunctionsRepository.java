package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.dto.LoteFifoDTO;
import DrinkGo.DrinkGo_backend.dto.ProductoProximoVencerDTO;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository para invocar las funciones PL/pgSQL de V2__database_functions.sql
 */
@Repository
public class DatabaseFunctionsRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // =========================================================================
    // Función 6: seleccionar_lotes_fifo
    // =========================================================================
    @SuppressWarnings("unchecked")
    public List<LoteFifoDTO> seleccionarLotesFifo(Long productoId, Long sedeId, BigDecimal cantidadRequerida) {
        String sql = "SELECT * FROM drinkgo.seleccionar_lotes_fifo(:productoId, :sedeId, :cantidad)";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("productoId", productoId);
        query.setParameter("sedeId", sedeId);
        query.setParameter("cantidad", cantidadRequerida);
        
        List<Object[]> results = query.getResultList();
        List<LoteFifoDTO> lotes = new ArrayList<>();
        
        for (Object[] row : results) {
            LoteFifoDTO dto = new LoteFifoDTO();
            dto.setLoteId(((Number) row[0]).longValue());
            dto.setCantidadADescontar((BigDecimal) row[1]);
            dto.setFechaVencimiento(row[2] != null ? ((Date) row[2]).toLocalDate() : null);
            lotes.add(dto);
        }
        
        return lotes;
    }
    
    // =========================================================================
    // Función 7: descontar_inventario_fifo
    // =========================================================================
    public void descontarInventarioFifo(Long productoId, Long sedeId, BigDecimal cantidad,
                                         String referenciaTipo, Long referenciaId) {
        String sql = "SELECT drinkgo.descontar_inventario_fifo(:productoId, :sedeId, :cantidad, :referenciaTipo, :referenciaId)";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("productoId", productoId);
        query.setParameter("sedeId", sedeId);
        query.setParameter("cantidad", cantidad);
        query.setParameter("referenciaTipo", referenciaTipo);
        query.setParameter("referenciaId", referenciaId);
        
        query.getSingleResult();
    }
    
    // =========================================================================
    // Función 8: obtener_productos_proximos_vencer
    // =========================================================================
    @SuppressWarnings("unchecked")
    public List<ProductoProximoVencerDTO> obtenerProductosProximosVencer(Long tenantId, Long sedeId, int diasAlerta) {
        String sql = "SELECT * FROM drinkgo.obtener_productos_proximos_vencer(:tenantId, :sedeId, :diasAlerta)";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("tenantId", tenantId);
        query.setParameter("sedeId", sedeId);
        query.setParameter("diasAlerta", diasAlerta);
        
        List<Object[]> results = query.getResultList();
        List<ProductoProximoVencerDTO> productos = new ArrayList<>();
        
        for (Object[] row : results) {
            ProductoProximoVencerDTO dto = new ProductoProximoVencerDTO();
            dto.setLoteId(((Number) row[0]).longValue());
            dto.setProductoId(((Number) row[1]).longValue());
            dto.setProductoNombre((String) row[2]);
            dto.setSedeId(((Number) row[3]).longValue());
            dto.setSedeNombre((String) row[4]);
            dto.setCantidadDisponible((BigDecimal) row[5]);
            dto.setFechaVencimiento(row[6] != null ? ((Date) row[6]).toLocalDate() : null);
            dto.setDiasRestantes(((Number) row[7]).intValue());
            dto.setNivelUrgencia((String) row[8]);
            productos.add(dto);
        }
        
        return productos;
    }
    
    // =========================================================================
    // Función 9: descontar_combo_inventario
    // =========================================================================
    public void descontarComboInventario(Long comboId, Long sedeId, int cantidadCombos,
                                          String referenciaTipo, Long referenciaId) {
        String sql = "SELECT drinkgo.descontar_combo_inventario(:comboId, :sedeId, :cantidadCombos, :referenciaTipo, :referenciaId)";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("comboId", comboId);
        query.setParameter("sedeId", sedeId);
        query.setParameter("cantidadCombos", cantidadCombos);
        query.setParameter("referenciaTipo", referenciaTipo);
        query.setParameter("referenciaId", referenciaId);
        
        query.getSingleResult();
    }
    
    // =========================================================================
    // Función 10: validar_horario_venta_alcohol
    // =========================================================================
    public boolean validarHorarioVentaAlcohol(Long sedeId) {
        String sql = "SELECT drinkgo.validar_horario_venta_alcohol(:sedeId)";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("sedeId", sedeId);
        
        return (Boolean) query.getSingleResult();
    }
    
    public boolean validarHorarioVentaAlcohol(Long sedeId, LocalTime horaActual) {
        String sql = "SELECT drinkgo.validar_horario_venta_alcohol(:sedeId, :horaActual)";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("sedeId", sedeId);
        query.setParameter("horaActual", horaActual);
        
        return (Boolean) query.getSingleResult();
    }
    
    // =========================================================================
    // Función 11: marcar_lotes_vencidos
    // =========================================================================
    public int marcarLotesVencidos() {
        String sql = "SELECT drinkgo.marcar_lotes_vencidos()";
        
        Query query = entityManager.createNativeQuery(sql);
        
        return ((Number) query.getSingleResult()).intValue();
    }
}
