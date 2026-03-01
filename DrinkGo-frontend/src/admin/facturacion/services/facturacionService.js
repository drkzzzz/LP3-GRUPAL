/**
 * facturacionService.js
 * ─────────────────────
 * Servicio para el módulo de Facturación (admin).
 * Consume los endpoints REST del AdminFacturacionController.
 */
import { adminApi } from '@/admin/services/adminApi';

const toArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  return [];
};

export const facturacionService = {
  /* ═══ SERIES ═══ */

  getSeries: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/series/negocio/${negocioId}`);
    return toArray(data);
  },

  getSerieById: async (id) => {
    const { data } = await adminApi.get(`/admin/facturacion/series/${id}`);
    return data;
  },

  crearSerie: async (serieData) => {
    const { data } = await adminApi.post('/admin/facturacion/series', serieData);
    return data;
  },

  actualizarSerie: async (id, serieData) => {
    const { data } = await adminApi.put(`/admin/facturacion/series/${id}`, serieData);
    return data;
  },

  eliminarSerie: async (id) => {
    const { data } = await adminApi.delete(`/admin/facturacion/series/${id}`);
    return data;
  },

  /* ═══ COMPROBANTES ═══ */

  getComprobantes: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/comprobantes/negocio/${negocioId}`);
    return toArray(data);
  },

  getComprobanteById: async (id) => {
    const { data } = await adminApi.get(`/admin/facturacion/comprobantes/${id}`);
    return data;
  },

  cambiarEstadoComprobante: async (id, estado) => {
    const { data } = await adminApi.patch(`/admin/facturacion/comprobantes/${id}/estado`, { estado });
    return data;
  },

  /* ═══ MÉTODOS DE PAGO ═══ */

  getMetodosPago: async (negocioId) => {
    const { data } = await adminApi.get(`/admin/facturacion/metodos-pago/negocio/${negocioId}`);
    return toArray(data);
  },

  getMetodoPagoById: async (id) => {
    const { data } = await adminApi.get(`/admin/facturacion/metodos-pago/${id}`);
    return data;
  },

  crearMetodoPago: async (metodoData) => {
    const { data } = await adminApi.post('/admin/facturacion/metodos-pago', metodoData);
    return data;
  },

  actualizarMetodoPago: async (id, metodoData) => {
    const { data } = await adminApi.put(`/admin/facturacion/metodos-pago/${id}`, metodoData);
    return data;
  },

  eliminarMetodoPago: async (id) => {
    const { data } = await adminApi.delete(`/admin/facturacion/metodos-pago/${id}`);
    return data;
  },
};
