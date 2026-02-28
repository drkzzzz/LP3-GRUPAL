/**
 * catalogoService.js
 * ──────────────────
 * Servicio centralizado del módulo Catálogo (Admin).
 * Consume los endpoints REST del backend Spring Boot.
 *
 * Endpoints base (todos bajo /restful):
 *   /productos, /categorias, /marcas, /unidades-medida,
 *   /combos, /detalle-combos, /promociones, /condiciones-promocion
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
 *  PRODUCTOS
 * ================================================================ */
export const productosService = {
  getAll: async (negocioId) => {
    const url = negocioId ? `/productos/negocio/${negocioId}` : '/productos';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/productos/${id}`);
    return data;
  },
  create: async (producto) => {
    const { data } = await adminApi.post('/productos', producto);
    return data;
  },
  update: async (producto) => {
    const { data } = await adminApi.put('/productos', producto);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/productos/${id}`);
    return data;
  },
};

/* ================================================================
 *  CATEGORÍAS
 * ================================================================ */
export const categoriasService = {
  getAll: async (negocioId) => {
    const url = negocioId ? `/categorias/negocio/${negocioId}` : '/categorias';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/categorias/${id}`);
    return data;
  },
  create: async (categoria) => {
    const { data } = await adminApi.post('/categorias', categoria);
    return data;
  },
  update: async (categoria) => {
    const { data } = await adminApi.put('/categorias', categoria);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/categorias/${id}`);
    return data;
  },
};

/* ================================================================
 *  MARCAS
 * ================================================================ */
export const marcasService = {
  getAll: async (negocioId) => {
    const url = negocioId ? `/marcas/negocio/${negocioId}` : '/marcas';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/marcas/${id}`);
    return data;
  },
  create: async (marca) => {
    const { data } = await adminApi.post('/marcas', marca);
    return data;
  },
  update: async (marca) => {
    const { data } = await adminApi.put('/marcas', marca);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/marcas/${id}`);
    return data;
  },
};

/* ================================================================
 *  UNIDADES DE MEDIDA
 * ================================================================ */
export const unidadesMedidaService = {
  getAll: async (negocioId) => {
    const url = negocioId ? `/unidades-medida/negocio/${negocioId}` : '/unidades-medida';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/unidades-medida/${id}`);
    return data;
  },
  create: async (unidad) => {
    const { data } = await adminApi.post('/unidades-medida', unidad);
    return data;
  },
  update: async (unidad) => {
    const { data } = await adminApi.put('/unidades-medida', unidad);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/unidades-medida/${id}`);
    return data;
  },
};

/* ================================================================
 *  COMBOS
 * ================================================================ */
export const combosService = {
  getAll: async (negocioId) => {
    const url = negocioId ? `/combos/negocio/${negocioId}` : '/combos';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/combos/${id}`);
    return data;
  },
  create: async (combo) => {
    const { data } = await adminApi.post('/combos', combo);
    return data;
  },
  update: async (combo) => {
    const { data } = await adminApi.put('/combos', combo);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/combos/${id}`);
    return data;
  },
};

/* ================================================================
 *  DETALLE COMBOS (productos dentro de un combo)
 * ================================================================ */
export const detalleCombosService = {
  getAll: async (negocioId) => {
    const url = negocioId ? `/detalle-combos/negocio/${negocioId}` : '/detalle-combos';
    const { data } = await adminApi.get(url);
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/detalle-combos/${id}`);
    return data;
  },
  create: async (detalle) => {
    const { data } = await adminApi.post('/detalle-combos', detalle);
    return data;
  },
  update: async (detalle) => {
    const { data } = await adminApi.put('/detalle-combos', detalle);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/detalle-combos/${id}`);
    return data;
  },
};

/* ================================================================
 *  PROMOCIONES (también cubre "Descuentos")
 * ================================================================ */
export const promocionesService = {
  getAll: async () => {
    const { data } = await adminApi.get('/promociones');
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/promociones/${id}`);
    return data;
  },
  create: async (promocion) => {
    const { data } = await adminApi.post('/promociones', promocion);
    return data;
  },
  update: async (promocion) => {
    const { data } = await adminApi.put('/promociones', promocion);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/promociones/${id}`);
    return data;
  },
};

/* ================================================================
 *  CONDICIONES DE PROMOCIÓN
 * ================================================================ */
export const condicionesPromocionService = {
  getAll: async () => {
    const { data } = await adminApi.get('/condiciones-promocion');
    return toArray(data);
  },
  getById: async (id) => {
    const { data } = await adminApi.get(`/condiciones-promocion/${id}`);
    return data;
  },
  create: async (condicion) => {
    const { data } = await adminApi.post('/condiciones-promocion', condicion);
    return data;
  },
  update: async (condicion) => {
    const { data } = await adminApi.put('/condiciones-promocion', condicion);
    return data;
  },
  delete: async (id) => {
    const { data } = await adminApi.delete(`/condiciones-promocion/${id}`);
    return data;
  },
};
