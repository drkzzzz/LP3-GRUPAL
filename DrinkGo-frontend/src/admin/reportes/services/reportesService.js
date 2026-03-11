/**
 * reportesService.js
 * ──────────────────
 * Servicio centralizado del módulo Reportes (Admin).
 * Agrega datos de los endpoints existentes para generar reportes.
 */
import { adminApi } from '@/admin/services/adminApi';

const toArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  if (Array.isArray(data.data)) return data.data;
  return [];
};

export const reportesService = {
  /* ═══ VENTAS ═══ */
  /**
   * Obtiene todas las ventas de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de ventas
   * @example
   * const ventas = await reportesService.getVentasByNegocio(5);
   */
  getVentasByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/ventas/negocio/${negocioId}`);
    return toArray(data);
  },

  /* ═══ STOCK INVENTARIO ═══ */
  /**
   * Obtiene el stock de inventario completo.
   * @returns {Promise<Array>} Lista de stock por producto
   * @example
   * const stock = await reportesService.getStockInventario();
   */
  getStockInventario: async (negocioId) => {
    const { data } = await adminApi.get(`/stock-inventario/negocio/${negocioId}`);
    return toArray(data);
  },

  /* ═══ LOTES INVENTARIO ═══ */
  /**
   * Obtiene todos los lotes de inventario.
   * @returns {Promise<Array>} Lista de lotes con fechas de vencimiento
   * @example
   * const lotes = await reportesService.getLotesInventario();
   */
  getLotesInventario: async (negocioId) => {
    const { data } = await adminApi.get(`/lotes-inventario/negocio/${negocioId}`);
    return toArray(data);
  },

  /* ═══ MOVIMIENTOS INVENTARIO ═══ */
  /**
   * Obtiene todos los movimientos de inventario.
   * @returns {Promise<Array>} Lista de entradas y salidas de inventario
   * @example
   * const movimientos = await reportesService.getMovimientosInventario();
   */
  getMovimientosInventario: async (negocioId) => {
    const { data } = await adminApi.get(`/movimientos-inventario/negocio/${negocioId}`);
    return toArray(data);
  },

  /* ═══ ÓRDENES DE COMPRA ═══ */
  /**
   * Obtiene todas las órdenes de compra.
   * @returns {Promise<Array>} Lista de órdenes de compra
   * @example
   * const ordenes = await reportesService.getOrdenesCompra();
   */
  getOrdenesCompra: async (negocioId) => {
    const { data } = await adminApi.get(`/ordenes-compra/negocio/${negocioId}`);
    return toArray(data);
  },

  /* ═══ GASTOS ═══ */
  /**
   * Obtiene los gastos de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de gastos filtrada (sin nulos)
   * @example
   * const gastos = await reportesService.getGastos(5);
   */
  getGastos: async (negocioId) => {
    const { data } = await adminApi.get(`/gastos/negocio/${negocioId}`);
    return Array.isArray(data) ? data.filter(Boolean) : [];
  },

  /* ═══ COMPROBANTES ═══ */
  /**
   * Obtiene los comprobantes (facturas, boletas) de un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de comprobantes emitidos
   * @example
   * const comprobantes = await reportesService.getComprobantes(5);
   */
  getComprobantes: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/comprobantes/negocio/${negocioId}`);
    return toArray(data);
  },

  /* ═══ DETALLE VENTAS POR NEGOCIO (para reporte productos) ═══ */
  /**
   * Obtiene el detalle completo de ventas de un negocio.
   * Incluye productos vendidos por cada venta.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Detalles de ventas con productos
   * @example
   * const detalles = await reportesService.getDetalleVentasByNegocio(5);
   */
  getDetalleVentasByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/ventas/negocio/${negocioId}/detalles`);
    return toArray(data);
  },

  /* ═══ PRODUCTOS (para cruzar con ventas) ═══ */
  /**
   * Obtiene todos los productos para cruzar con datos de ventas.
   * @returns {Promise<Array>} Lista de productos
   * @example
   * const productos = await reportesService.getProductos();
   */
  getProductos: async () => {
    const { data } = await adminApi.get('/productos');
    return toArray(data);
  },
};
