/**
 * inventarioService.js
 * ────────────────────
 * Servicio centralizado del módulo Inventario (Admin).
 * Consume los endpoints REST del backend Spring Boot.
 *
 * Endpoints base (todos bajo /restful):
 *   /stock-inventario, /lotes-inventario, /movimientos-inventario, /almacenes
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
 *  STOCK INVENTARIO
 * ================================================================ */
export const stockInventarioService = {
  getAll: async () => {
    const { data } = await adminApi.get('/stock-inventario');
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/stock-inventario/${id}`);
    return data;
  },
  create: async (stock) => {
    const { data } = await adminApi.post('/stock-inventario', stock);
    return data;
  },
  update: async (stock) => {
    const { data } = await adminApi.put('/stock-inventario', stock);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/stock-inventario/${id}`);
    return data;
  },
};

/* ================================================================
 *  LOTES DE INVENTARIO
 * ================================================================ */
export const lotesInventarioService = {
  getAll: async () => {
    const { data } = await adminApi.get('/lotes-inventario');
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/lotes-inventario/${id}`);
    return data;
  },
  create: async (lote) => {
    const { data } = await adminApi.post('/lotes-inventario', lote);
    return data;
  },
  update: async (lote) => {
    const { data } = await adminApi.put('/lotes-inventario', lote);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/lotes-inventario/${id}`);
    return data;
  },
};

/* ================================================================
 *  MOVIMIENTOS DE INVENTARIO
 * ================================================================ */
export const movimientosInventarioService = {
  getAll: async () => {
    const { data } = await adminApi.get('/movimientos-inventario');
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/movimientos-inventario/${id}`);
    return data;
  },
  create: async (movimiento) => {
    const { data } = await adminApi.post('/movimientos-inventario', movimiento);
    return data;
  },
  update: async (movimiento) => {
    const { data } = await adminApi.put('/movimientos-inventario', movimiento);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/movimientos-inventario/${id}`);
    return data;
  },
};

/* ================================================================
 *  ALMACENES
 * ================================================================ */
export const almacenesService = {
  getAll: async () => {
    const { data } = await adminApi.get('/almacenes');
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/almacenes/${id}`);
    return data;
  },
  create: async (almacen) => {
    const { data } = await adminApi.post('/almacenes', almacen);
    return data;
  },
  update: async (almacen) => {
    const { data } = await adminApi.put('/almacenes', almacen);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/almacenes/${id}`);
    return data;
  },
};

/* ================================================================
 *  SEDES (solo lectura, para selects de almacenes)
 * ================================================================ */
export const sedesService = {
  getAll: async () => {
    const { data } = await adminApi.get('/sedes');
    return toArray(data);
  },
};

/* ================================================================
 *  PRODUCTOS (solo lectura, para selects de inventario)
 * ================================================================ */
export const productosInventarioService = {
  getAll: async (negocioId) => {
    const url = negocioId ? `/productos/negocio/${negocioId}` : '/productos';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },
};
