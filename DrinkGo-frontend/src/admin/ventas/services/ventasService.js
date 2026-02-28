/**
 * ventasService.js
 * ────────────────
 * Servicio para el módulo de Ventas POS (admin).
 * Consume los endpoints REST del PosController.
 */
import { adminApi } from '@/admin/services/adminApi';

const toArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  return [];
};

export const ventasService = {
  /* ─── Listar ventas por negocio ─── */
  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/ventas/negocio/${negocioId}`);
    return toArray(data);
  },

  /* ─── Listar ventas por sesión de caja ─── */
  getBySesion: async (sesionCajaId) => {
    const { data } = await adminApi.get(`/pos/ventas/sesion/${sesionCajaId}`);
    return toArray(data);
  },

  /* ─── Obtener una venta ─── */
  getById: async (ventaId, negocioId) => {
    const { data } = await adminApi.get(`/pos/ventas/${ventaId}/negocio/${negocioId}`);
    return data;
  },

  /* ─── Obtener detalle de venta ─── */
  getDetalle: async (ventaId) => {
    const { data } = await adminApi.get(`/pos/ventas/${ventaId}/detalle`);
    return toArray(data);
  },

  /* ─── Obtener pagos de venta ─── */
  getPagos: async (ventaId) => {
    const { data } = await adminApi.get(`/pos/ventas/${ventaId}/pagos`);
    return toArray(data);
  },

  /* ─── Crear venta POS ─── */
  crear: async (ventaData) => {
    const { data } = await adminApi.post('/pos/ventas', ventaData);
    return data;
  },

  /* ─── Anular venta ─── */
  anular: async (anularData) => {
    const { data } = await adminApi.post('/pos/ventas/anular', anularData);
    return data;
  },

  /* ─── Métodos de pago disponibles para POS ─── */
  getMetodosPago: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/metodos-pago/negocio/${negocioId}`);
    return toArray(data);
  },
};
