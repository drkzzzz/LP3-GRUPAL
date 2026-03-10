/**
 * devolucionesService.js
 * ──────────────────────
 * Servicio del módulo Devoluciones (Admin).
 * Consume los endpoints REST del backend Spring Boot.
 *
 * Endpoints base (todos bajo /restful):
 *   /devoluciones, /detalle-devoluciones
 */
import { adminApi } from '@/admin/services/adminApi';

/* ─── Helper: normaliza respuesta a arreglo ─── */
const toArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  if (Array.isArray(data.data)) return data.data;
  return [];
};

/* ================================================================
 *  DEVOLUCIONES
 * ================================================================ */
export const devolucionesService = {
  getAll: async () => {
    const { data } = await adminApi.get('/devoluciones');
    return toArray(data);
  },

  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/devoluciones/negocio/${negocioId}`);
    return toArray(data);
  },

  getByVenta: async (ventaId) => {
    const { data } = await adminApi.get(`/devoluciones/venta/${ventaId}`);
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await adminApi.get(`/devoluciones/${id}`);
    return data;
  },

  create: async (devolucion) => {
    const { data } = await adminApi.post('/devoluciones', devolucion);
    return data;
  },

  update: async (devolucion) => {
    const { data } = await adminApi.put('/devoluciones', devolucion);
    return data;
  },

  delete: async (id) => {
    const { data } = await adminApi.delete(`/devoluciones/${id}`);
    return data;
  },

  /** Aprobar devolución (solo admin) */
  aprobar: async (id, usuarioId) => {
    const { data } = await adminApi.patch(`/devoluciones/${id}/aprobar?usuarioId=${usuarioId}`);
    return data;
  },

  /** Rechazar devolución (solo admin) */
  rechazar: async (id, usuarioId, razon) => {
    const { data } = await adminApi.patch(
      `/devoluciones/${id}/rechazar?usuarioId=${usuarioId}`,
      { razon },
    );
    return data;
  },
};

/* ================================================================
 *  DETALLE DEVOLUCIONES
 * ================================================================ */
export const detalleDevolucionesService = {
  getAll: async () => {
    const { data } = await adminApi.get('/detalle-devoluciones');
    return toArray(data);
  },

  getByDevolucion: async (devolucionId) => {
    const { data } = await adminApi.get(`/detalle-devoluciones/devolucion/${devolucionId}`);
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await adminApi.get(`/detalle-devoluciones/${id}`);
    return data;
  },

  create: async (detalle) => {
    const { data } = await adminApi.post('/detalle-devoluciones', detalle);
    return data;
  },

  update: async (detalle) => {
    const { data } = await adminApi.put('/detalle-devoluciones', detalle);
    return data;
  },

  delete: async (id) => {
    const { data } = await adminApi.delete(`/detalle-devoluciones/${id}`);
    return data;
  },
};
