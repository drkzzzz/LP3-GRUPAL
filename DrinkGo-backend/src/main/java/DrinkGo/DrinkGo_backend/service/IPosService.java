package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.pos.*;
import DrinkGo.DrinkGo_backend.entity.*;

import java.util.List;
import java.util.Map;

/**
 * Servicio del módulo POS (Punto de Venta).
 * Orquesta la lógica de negocio de ventas, cajas y sesiones.
 */
public interface IPosService {

    // ─── Cajas Registradoras ───
    List<CajasRegistradoras> listarCajasPorNegocio(Long negocioId);

    CajasRegistradoras obtenerCaja(Long cajaId, Long negocioId);

    CajasRegistradoras crearCaja(CrearCajaRequest request);

    CajasRegistradoras actualizarCaja(CrearCajaRequest request);

    // ─── Sesiones de Caja (Turnos) ───
    SesionesCaja abrirCaja(AbrirCajaRequest request);

    SesionesCaja cerrarCaja(CerrarCajaRequest request);

    SesionesCaja obtenerSesionActiva(Long usuarioId);

    List<SesionesCaja> listarSesionesPorNegocio(Long negocioId);

    List<SesionesCaja> listarSesionesPorCaja(Long cajaId);

    // ─── Movimientos de Caja ───
    MovimientosCaja registrarMovimiento(MovimientoCajaRequest request);

    List<MovimientosCaja> listarMovimientosSesion(Long sesionCajaId);

    // ─── Ventas POS ───
    Ventas crearVentaPos(CrearVentaPosRequest request);

    Ventas anularVenta(AnularVentaRequest request);

    Ventas obtenerVenta(Long ventaId, Long negocioId);

    List<Ventas> listarVentasPorNegocio(Long negocioId);

    List<Ventas> listarVentasPorSesion(Long sesionCajaId);

    List<DetalleVentas> obtenerDetalleVenta(Long ventaId);

    List<PagosVenta> obtenerPagosVenta(Long ventaId);

    // ─── Métodos de Pago ───
    List<MetodosPago> listarMetodosPagoPOS(Long negocioId);

    // ─── Resumen de turno ───
    Map<String, Object> obtenerResumenTurno(Long sesionCajaId);
}
