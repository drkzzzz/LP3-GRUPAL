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
  getVentasByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/ventas/negocio/${negocioId}`);
    return toArray(data);
  },

  /* ═══ STOCK INVENTARIO ═══ */
  getStockInventario: async () => {
    const { data } = await adminApi.get('/stock-inventario');
    return toArray(data);
  },

  /* ═══ LOTES INVENTARIO ═══ */
  getLotesInventario: async () => {
    const { data } = await adminApi.get('/lotes-inventario');
    return toArray(data);
  },

  /* ═══ MOVIMIENTOS INVENTARIO ═══ */
  getMovimientosInventario: async () => {
    const { data } = await adminApi.get('/movimientos-inventario');
    return toArray(data);
  },

  /* ═══ ÓRDENES DE COMPRA ═══ */
  getOrdenesCompra: async () => {
    const { data } = await adminApi.get('/ordenes-compra');
    return toArray(data);
  },

  /* ═══ GASTOS ═══ */
  getGastos: async (negocioId) => {
    const { data } = await adminApi.get(`/gastos/negocio/${negocioId}`);
    return Array.isArray(data) ? data.filter(Boolean) : [];
  },

  /* ═══ COMPROBANTES ═══ */
  getComprobantes: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/comprobantes/negocio/${negocioId}`);
    return toArray(data);
  },

  /* ═══ DETALLE VENTAS POR NEGOCIO (para reporte productos) ═══ */
  getDetalleVentasByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/ventas/negocio/${negocioId}/detalles`);
    return toArray(data);
  },

  /* ═══ PRODUCTOS (para cruzar con ventas) ═══ */
  getProductos: async () => {
    const { data } = await adminApi.get('/productos');
    return toArray(data);
  },
};
