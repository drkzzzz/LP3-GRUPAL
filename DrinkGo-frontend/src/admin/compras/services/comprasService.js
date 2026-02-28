/**
 * comprasService.js
 * ─────────────────
 * Servicio centralizado del módulo Proveedores y Compras (Admin).
 * Consume los endpoints REST del backend Spring Boot.
 *
 * Endpoints base (todos bajo /restful):
 *   /proveedores, /productos-proveedor, /ordenes-compra, /detalle-ordenes-compra
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
 *  PROVEEDORES
 * ================================================================ */
export const proveedoresService = {
  getAll: async () => {
    const { data } = await adminApi.get('/proveedores');
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/proveedores/${id}`);
    return data;
  },
  create: async (proveedor) => {
    const { data } = await adminApi.post('/proveedores', proveedor);
    return data;
  },
  update: async (proveedor) => {
    const { data } = await adminApi.put('/proveedores', proveedor);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/proveedores/${id}`);
    return data;
  },
};

/* ================================================================
 *  PRODUCTOS POR PROVEEDOR
 * ================================================================ */
export const productosProveedorService = {
  getAll: async () => {
    const { data } = await adminApi.get('/productos-proveedor');
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/productos-proveedor/${id}`);
    return data;
  },
  create: async (productoProveedor) => {
    const { data } = await adminApi.post('/productos-proveedor', productoProveedor);
    return data;
  },
  update: async (productoProveedor) => {
    const { data } = await adminApi.put('/productos-proveedor', productoProveedor);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/productos-proveedor/${id}`);
    return data;
  },
};

/* ================================================================
 *  ÓRDENES DE COMPRA
 * ================================================================ */
export const ordenesCompraService = {
  getAll: async () => {
    const { data } = await adminApi.get('/ordenes-compra');
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/ordenes-compra/${id}`);
    return data;
  },
  create: async (orden) => {
    const { data } = await adminApi.post('/ordenes-compra', orden);
    return data;
  },
  update: async (orden) => {
    const { data } = await adminApi.put('/ordenes-compra', orden);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/ordenes-compra/${id}`);
    return data;
  },
};

/* ================================================================
 *  DETALLE DE ÓRDENES DE COMPRA
 * ================================================================ */
export const detalleOrdenesCompraService = {
  getAll: async () => {
    const { data } = await adminApi.get('/detalle-ordenes-compra');
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/detalle-ordenes-compra/${id}`);
    return data;
  },
  create: async (detalle) => {
    const { data } = await adminApi.post('/detalle-ordenes-compra', detalle);
    return data;
  },
  update: async (detalle) => {
    const { data } = await adminApi.put('/detalle-ordenes-compra', detalle);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/detalle-ordenes-compra/${id}`);
    return data;
  },
};

/* ================================================================
 *  SEDES (solo lectura, para selects)
 * ================================================================ */
export const sedesComprasService = {
  getAll: async () => {
    const { data } = await adminApi.get('/sedes');
    return toArray(data);
  },
};

/* ================================================================
 *  ALMACENES (solo lectura, para selects)
 * ================================================================ */
export const almacenesComprasService = {
  getAll: async () => {
    const { data } = await adminApi.get('/almacenes');
    return toArray(data);
  },
};

/* ================================================================
 *  PRODUCTOS (solo lectura, para selects en órdenes)
 * ================================================================ */
export const productosComprasService = {
  getAll: async (negocioId) => {
    const url = negocioId ? `/productos/negocio/${negocioId}` : '/productos';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },
};
