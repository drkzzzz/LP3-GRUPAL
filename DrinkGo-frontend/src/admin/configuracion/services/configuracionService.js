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
  /**
   * Obtiene los datos de un negocio por su ID.
   * @param {number} id - ID del negocio
   * @returns {Promise<Object>} Datos del negocio
   * @example
   * const negocio = await negocioService.getById(5);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/negocios/${id}`);
    return data;
  },

  /**
   * Actualiza los datos del negocio.
   * @param {Object} negocio - Datos del negocio a actualizar
   * @returns {Promise<Object>} Negocio actualizado
   * @example
   * await negocioService.update({ id: 5, nombre: 'Mi Licorería' });
   */
  update: async (negocio) => {
    const { data } = await adminApi.put('/negocios', negocio);
    return data;
  },
};

/* ================================================================
 *  SEDES
 * ================================================================ */
export const sedesService = {
  /**
   * Obtiene todas las sedes.
   * @returns {Promise<Array>} Lista de sedes
   * @example
   * const sedes = await sedesService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/sedes');
    return toArray(data);
  },

  /**
   * Obtiene una sede por su ID.
   * @param {number} id - ID de la sede
   * @returns {Promise<Object>} Sede encontrada
   * @example
   * const sede = await sedesService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/sedes/${id}`);
    return data;
  },

  /**
   * Crea una nueva sede.
   * @param {Object} sede - Datos de la sede (nombre, dirección)
   * @returns {Promise<Object>} Sede creada
   * @example
   * const sede = await sedesService.create({ nombre: 'Sede Centro', direccion: 'Av. Principal 123' });
   */
  create: async (sede) => {
    const { data } = await adminApi.post('/sedes', sede);
    return data;
  },

  /**
   * Actualiza una sede existente.
   * @param {Object} sede - Datos de la sede a actualizar
   * @returns {Promise<Object>} Sede actualizada
   * @example
   * await sedesService.update({ id: 1, nombre: 'Sede Norte' });
   */
  update: async (sede) => {
    const { data } = await adminApi.put('/sedes', sede);
    return data;
  },

  /**
   * Elimina una sede.
   * @param {number} id - ID de la sede a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await sedesService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/sedes/${id}`);
    return data;
  },
};

/* ================================================================
 *  ALMACENES
 * ================================================================ */
export const almacenesConfigService = {
  /**
   * Obtiene todos los almacenes.
   * @returns {Promise<Array>} Lista de almacenes
   * @example
   * const almacenes = await almacenesConfigService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/almacenes');
    return toArray(data);
  },

  /**
   * Obtiene un almacén por su ID.
   * @param {number} id - ID del almacén
   * @returns {Promise<Object>} Almacén encontrado
   * @example
   * const almacen = await almacenesConfigService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/almacenes/${id}`);
    return data;
  },

  /**
   * Crea un nuevo almacén.
   * @param {Object} almacen - Datos del almacén
   * @returns {Promise<Object>} Almacén creado
   * @example
   * const nuevo = await almacenesConfigService.create({ nombre: 'Almacén Principal', sedeId: 1 });
   */
  create: async (almacen) => {
    const { data } = await adminApi.post('/almacenes', almacen);
    return data;
  },

  /**
   * Actualiza un almacén existente.
   * @param {Object} almacen - Datos del almacén a actualizar
   * @returns {Promise<Object>} Almacén actualizado
   * @example
   * await almacenesConfigService.update({ id: 1, nombre: 'Almacén Secundario' });
   */
  update: async (almacen) => {
    const { data } = await adminApi.put('/almacenes', almacen);
    return data;
  },

  /**
   * Elimina un almacén.
   * @param {number} id - ID del almacén a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await almacenesConfigService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/almacenes/${id}`);
    return data;
  },
};

/* ================================================================
 *  MÉTODOS DE PAGO
 * ================================================================ */
export const metodosPagoService = {
  /**
   * Obtiene todos los métodos de pago.
   * @returns {Promise<Array>} Lista de métodos de pago
   * @example
   * const metodos = await metodosPagoService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/metodos-pago');
    return toArray(data);
  },

  /**
   * Obtiene un método de pago por su ID.
   * @param {number} id - ID del método de pago
   * @returns {Promise<Object>} Método de pago encontrado
   * @example
   * const metodo = await metodosPagoService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/metodos-pago/${id}`);
    return data;
  },

  /**
   * Crea un nuevo método de pago.
   * @param {Object} metodo - Datos del método (efectivo, tarjeta, etc.)
   * @returns {Promise<Object>} Método creado
   * @example
   * const metodo = await metodosPagoService.create({ nombre: 'Yape', tipo: 'DIGITAL' });
   */
  create: async (metodo) => {
    const { data } = await adminApi.post('/metodos-pago', metodo);
    return data;
  },

  /**
   * Actualiza un método de pago existente.
   * @param {Object} metodo - Datos del método a actualizar
   * @returns {Promise<Object>} Método actualizado
   * @example
   * await metodosPagoService.update({ id: 1, nombre: 'Efectivo USD' });
   */
  update: async (metodo) => {
    const { data } = await adminApi.put('/metodos-pago', metodo);
    return data;
  },

  /**
   * Elimina un método de pago.
   * @param {number} id - ID del método a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await metodosPagoService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/metodos-pago/${id}`);
    return data;
  },
};

/* ================================================================
 *  ZONAS DE DELIVERY
 * ================================================================ */
export const zonasDeliveryService = {
  /**
   * Obtiene todas las zonas de delivery.
   * @returns {Promise<Array>} Lista de zonas
   * @example
   * const zonas = await zonasDeliveryService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/zonas-delivery');
    return toArray(data);
  },

  /**
   * Obtiene una zona de delivery por su ID.
   * @param {number} id - ID de la zona
   * @returns {Promise<Object>} Zona encontrada
   * @example
   * const zona = await zonasDeliveryService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/zonas-delivery/${id}`);
    return data;
  },

  /**
   * Crea una nueva zona de delivery.
   * @param {Object} zona - Datos de la zona (nombre, tarifa)
   * @returns {Promise<Object>} Zona creada
   * @example
   * const zona = await zonasDeliveryService.create({ nombre: 'Centro', tarifa: 5.00 });
   */
  create: async (zona) => {
    const { data } = await adminApi.post('/zonas-delivery', zona);
    return data;
  },

  /**
   * Actualiza una zona de delivery existente.
   * @param {Object} zona - Datos de la zona a actualizar
   * @returns {Promise<Object>} Zona actualizada
   * @example
   * await zonasDeliveryService.update({ id: 1, tarifa: 7.00 });
   */
  update: async (zona) => {
    const { data } = await adminApi.put('/zonas-delivery', zona);
    return data;
  },

  /**
   * Elimina una zona de delivery.
   * @param {number} id - ID de la zona a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await zonasDeliveryService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/zonas-delivery/${id}`);
    return data;
  },
};

/* ================================================================
 *  MESAS
 * ================================================================ */
export const mesasService = {
  /**
   * Obtiene todas las mesas.
   * @returns {Promise<Array>} Lista de mesas
   * @example
   * const mesas = await mesasService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/mesas');
    return toArray(data);
  },

  /**
   * Obtiene las mesas de un negocio específico.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de mesas del negocio
   * @example
   * const mesas = await mesasService.getByNegocioId(5);
   */
  getByNegocioId: async (negocioId) => {
    const { data } = await adminApi.get(`/mesas/por-negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Obtiene una mesa por su ID.
   * @param {number} id - ID de la mesa
   * @returns {Promise<Object>} Mesa encontrada
   * @example
   * const mesa = await mesasService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/mesas/${id}`);
    return data;
  },

  /**
   * Crea una nueva mesa.
   * @param {Object} mesa - Datos de la mesa (número, capacidad)
   * @returns {Promise<Object>} Mesa creada
   * @example
   * const mesa = await mesasService.create({ numero: 5, capacidad: 4 });
   */
  create: async (mesa) => {
    const { data } = await adminApi.post('/mesas', mesa);
    return data;
  },

  /**
   * Actualiza una mesa existente.
   * @param {Object} mesa - Datos de la mesa a actualizar
   * @returns {Promise<Object>} Mesa actualizada
   * @example
   * await mesasService.update({ id: 1, capacidad: 6 });
   */
  update: async (mesa) => {
    const { data } = await adminApi.put('/mesas', mesa);
    return data;
  },

  /**
   * Elimina una mesa.
   * @param {number} id - ID de la mesa a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await mesasService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/mesas/${id}`);
    return data;
  },
};

/* ================================================================
 *  CAJAS REGISTRADORAS
 * ================================================================ */
export const cajasRegistradorasService = {
  /**
   * Obtiene todas las cajas registradoras.
   * @returns {Promise<Array>} Lista de cajas
   * @example
   * const cajas = await cajasRegistradorasService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/cajas-registradoras');
    return toArray(data);
  },

  /**
   * Obtiene una caja registradora por su ID.
   * @param {number} id - ID de la caja
   * @returns {Promise<Object>} Caja encontrada
   * @example
   * const caja = await cajasRegistradorasService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/cajas-registradoras/${id}`);
    return data;
  },

  /**
   * Crea una nueva caja registradora.
   * @param {Object} caja - Datos de la caja (nombre, sede)
   * @returns {Promise<Object>} Caja creada
   * @example
   * const caja = await cajasRegistradorasService.create({ nombre: 'Caja 1', sedeId: 1 });
   */
  create: async (caja) => {
    const { data } = await adminApi.post('/cajas-registradoras', caja);
    return data;
  },

  /**
   * Actualiza una caja registradora existente.
   * @param {Object} caja - Datos de la caja a actualizar
   * @returns {Promise<Object>} Caja actualizada
   * @example
   * await cajasRegistradorasService.update({ id: 1, nombre: 'Caja Principal' });
   */
  update: async (caja) => {
    const { data } = await adminApi.put('/cajas-registradoras', caja);
    return data;
  },

  /**
   * Elimina una caja registradora.
   * @param {number} id - ID de la caja a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await cajasRegistradorasService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/cajas-registradoras/${id}`);
    return data;
  },
};

/* ================================================================
 *  CONFIGURACIÓN TIENDA ONLINE
 * ================================================================ */
export const tiendaOnlineService = {
  /**
   * Obtiene todas las configuraciones de tienda online.
   * @returns {Promise<Array>} Lista de configuraciones
   * @example
   * const configs = await tiendaOnlineService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/configuracion-tienda-online');
    return toArray(data);
  },

  /**
   * Obtiene una configuración de tienda online por su ID.
   * @param {number} id - ID de la configuración
   * @returns {Promise<Object>} Configuración encontrada
   * @example
   * const config = await tiendaOnlineService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/configuracion-tienda-online/${id}`);
    return data;
  },

  /**
   * Crea una nueva configuración de tienda online.
   * @param {Object} config - Datos de configuración (horarios, delivery, pagos)
   * @returns {Promise<Object>} Configuración creada
   * @example
   * const config = await tiendaOnlineService.create({ habilitarDelivery: true });
   */
  create: async (config) => {
    const { data } = await adminApi.post('/configuracion-tienda-online', config);
    return data;
  },

  /**
   * Actualiza una configuración de tienda online existente.
   * @param {Object} config - Datos de configuración a actualizar
   * @returns {Promise<Object>} Configuración actualizada
   * @example
   * await tiendaOnlineService.update({ id: 1, habilitarDelivery: false });
   */
  update: async (config) => {
    const { data } = await adminApi.put('/configuracion-tienda-online', config);
    return data;
  },

  /**
   * Elimina una configuración de tienda online.
   * @param {number} id - ID de la configuración a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await tiendaOnlineService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/configuracion-tienda-online/${id}`);
    return data;
  },
};
