/**
 * configuracionService.js
 * ───────────────────────
 * Servicio centralizado del módulo Configuración (Admin).
 * Consume los endpoints REST del backend Spring Boot.
 *
 * Endpoints base (todos bajo /restful):
 *   /negocios, /sedes, /almacenes, /metodos-pago,
 *   /zonas-delivery, /mesas, /cajas-registradoras,
 *   /configuracion-tienda-online
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
 *  NEGOCIO (datos del negocio actual)
 * ================================================================ */
export const negocioService = {
  getById: async (id) => {
    const { data } = await adminApi.get(`/negocios/${id}`);
    return data;
  },

  update: async (negocio) => {
    const { data } = await adminApi.put('/negocios', negocio);
    return data;
  },
};

/* ================================================================
 *  SEDES
 * ================================================================ */
export const sedesService = {
  getAll: async () => {
    const { data } = await adminApi.get('/sedes');
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await adminApi.get(`/sedes/${id}`);
    return data;
  },

  create: async (sede) => {
    const { data } = await adminApi.post('/sedes', sede);
    return data;
  },

  update: async (sede) => {
    const { data } = await adminApi.put('/sedes', sede);
    return data;
  },

  delete: async (id) => {
    const { data } = await adminApi.delete(`/sedes/${id}`);
    return data;
  },
};

/* ================================================================
 *  ALMACENES
 * ================================================================ */
export const almacenesConfigService = {
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
 *  MÉTODOS DE PAGO
 * ================================================================ */
export const metodosPagoService = {
  getAll: async () => {
    const { data } = await adminApi.get('/metodos-pago');
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await adminApi.get(`/metodos-pago/${id}`);
    return data;
  },

  create: async (metodo) => {
    const { data } = await adminApi.post('/metodos-pago', metodo);
    return data;
  },

  update: async (metodo) => {
    const { data } = await adminApi.put('/metodos-pago', metodo);
    return data;
  },

  delete: async (id) => {
    const { data } = await adminApi.delete(`/metodos-pago/${id}`);
    return data;
  },
};

/* ================================================================
 *  ZONAS DE DELIVERY
 * ================================================================ */
export const zonasDeliveryService = {
  getAll: async () => {
    const { data } = await adminApi.get('/zonas-delivery');
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await adminApi.get(`/zonas-delivery/${id}`);
    return data;
  },

  create: async (zona) => {
    const { data } = await adminApi.post('/zonas-delivery', zona);
    return data;
  },

  update: async (zona) => {
    const { data } = await adminApi.put('/zonas-delivery', zona);
    return data;
  },

  delete: async (id) => {
    const { data } = await adminApi.delete(`/zonas-delivery/${id}`);
    return data;
  },
};

/* ================================================================
 *  MESAS
 * ================================================================ */
export const mesasService = {
  getAll: async () => {
    const { data } = await adminApi.get('/mesas');
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await adminApi.get(`/mesas/${id}`);
    return data;
  },

  create: async (mesa) => {
    const { data } = await adminApi.post('/mesas', mesa);
    return data;
  },

  update: async (mesa) => {
    const { data } = await adminApi.put('/mesas', mesa);
    return data;
  },

  delete: async (id) => {
    const { data } = await adminApi.delete(`/mesas/${id}`);
    return data;
  },
};

/* ================================================================
 *  CAJAS REGISTRADORAS
 * ================================================================ */
export const cajasRegistradorasService = {
  getAll: async () => {
    const { data } = await adminApi.get('/cajas-registradoras');
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await adminApi.get(`/cajas-registradoras/${id}`);
    return data;
  },

  create: async (caja) => {
    const { data } = await adminApi.post('/cajas-registradoras', caja);
    return data;
  },

  update: async (caja) => {
    const { data } = await adminApi.put('/cajas-registradoras', caja);
    return data;
  },

  delete: async (id) => {
    const { data } = await adminApi.delete(`/cajas-registradoras/${id}`);
    return data;
  },
};

/* ================================================================
 *  CONFIGURACIÓN TIENDA ONLINE
 * ================================================================ */
export const tiendaOnlineService = {
  getAll: async () => {
    const { data } = await adminApi.get('/configuracion-tienda-online');
    return toArray(data);
  },

  getById: async (id) => {
    const { data } = await adminApi.get(`/configuracion-tienda-online/${id}`);
    return data;
  },

  create: async (config) => {
    const { data } = await adminApi.post('/configuracion-tienda-online', config);
    return data;
  },

  update: async (config) => {
    const { data } = await adminApi.put('/configuracion-tienda-online', config);
    return data;
  },

  delete: async (id) => {
    const { data } = await adminApi.delete(`/configuracion-tienda-online/${id}`);
    return data;
  },
};
