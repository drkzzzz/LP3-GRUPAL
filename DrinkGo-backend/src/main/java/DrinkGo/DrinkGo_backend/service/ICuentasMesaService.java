package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import DrinkGo.DrinkGo_backend.entity.CuentasMesa;
import DrinkGo.DrinkGo_backend.entity.DetalleCuentaMesa;

public interface ICuentasMesaService {

    /** RF-VTA-011: Abre una cuenta para una mesa */
    CuentasMesa abrirCuenta(CuentasMesa cuenta);

    /** Lista cuentas abiertas de una sede */
    List<CuentasMesa> buscarAbiertasPorSede(Long sedeId);

    /** Obtiene una cuenta con sus detalles */
    Optional<CuentasMesa> buscarPorId(Long id);

    /** RF-VTA-012: Agrega un producto a la cuenta */
    DetalleCuentaMesa agregarProducto(Long cuentaId, DetalleCuentaMesa detalle);

    /** RF-VTA-012: Elimina (soft-delete) un ítem de la cuenta */
    void removerProducto(Long detalleId);

    /** RF-VTA-012: Lista los ítems activos de una cuenta */
    List<DetalleCuentaMesa> obtenerDetalles(Long cuentaId);

    /**
     * RF-VTA-013: Transfiere productos seleccionados a otra cuenta (misma u otra
     * mesa).
     * Si la cuenta destino está vacía se puede pasar cuentaDestinoId = null
     * y se creará una nueva cuenta en nuevaMesaId.
     */
    void transferirProductos(Long cuentaOrigenId, Long cuentaDestinoId, List<Long> detalleIds);

    /** RF-VTA-013: Mueve la cuenta completa a otra mesa */
    CuentasMesa transferirMesa(Long cuentaId, Long nuevaMesaId);

    /**
     * RF-VTA-014: Divide la cuenta por número de personas.
     * Retorna una lista de mapas con los ítems por cada división.
     */
    List<Map<String, Object>> dividirPorPersonas(Long cuentaId, int personas);

    /**
     * RF-VTA-014: Cierra la cuenta, registra el pago y libera la mesa.
     * 
     * @param cuentaId  id de la cuenta
     * @param usuarioId id del cajero/mesero que cierra
     */
    CuentasMesa cerrarCuenta(Long cuentaId, Long usuarioId);
}
