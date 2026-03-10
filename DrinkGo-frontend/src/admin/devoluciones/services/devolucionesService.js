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
  /**
   * Obtiene todas las devoluciones del sistema.
   * @returns {Promise<Array>} Lista de devoluciones
   * @example
   * const devoluciones = await devolucionesService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/devoluciones');
    return toArray(data);
  },

  /**
   * Obtiene las devoluciones de un negocio específico.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de devoluciones del negocio
   * @example
   * const devoluciones = await devolucionesService.getByNegocio(5);
   */
  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/devoluciones/negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Obtiene las devoluciones asociadas a una venta.
   * @param {number} ventaId - ID de la venta
   * @returns {Promise<Array>} Lista de devoluciones de la venta
   * @example
   * const devoluciones = await devolucionesService.getByVenta(100);
   */
  getByVenta: async (ventaId) => {
    const { data } = await adminApi.get(`/devoluciones/venta/${ventaId}`);
    return toArray(data);
  },

  /**
   * Obtiene una devolución por su ID.
   * @param {number} id - ID de la devolución
   * @returns {Promise<Object>} Devolución encontrada
   * @example
   * const devolucion = await devolucionesService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/devoluciones/${id}`);
    return data;
  },

  /**
   * Crea una nueva devolución.
   * @param {Object} devolucion - Datos de la devolución a crear
   * @returns {Promise<Object>} Devolución creada
   * @example
   * const nueva = await devolucionesService.create({
   *   ventaId: 100,
   *   motivo: 'Producto defectuoso'
   * });
   */
  create: async (devolucion) => {
    const { data } = await adminApi.post('/devoluciones', devolucion);
    return data;
  },

  /**
   * Actualiza una devolución existente.
   * @param {Object} devolucion - Datos de la devolución a actualizar
   * @returns {Promise<Object>} Devolución actualizada
   * @example
   * const actualizada = await devolucionesService.update({ devolucionId: 1, ... });
   */
  update: async (devolucion) => {
    const { data } = await adminApi.put('/devoluciones', devolucion);
    return data;
  },

  /**
   * Elimina una devolución.
   * @param {number} id - ID de la devolución a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await devolucionesService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/devoluciones/${id}`);
    return data;
  },

  /**
   * Aprueba una devolución (solo admin).
   * @param {number} id - ID de la devolución
   * @param {number} usuarioId - ID del usuario que aprueba
   * @returns {Promise<Object>} Devolución aprobada
   * @example
   * await devolucionesService.aprobar(1, 3);
   */
  aprobar: async (id, usuarioId) => {
    const { data } = await adminApi.patch(`/devoluciones/${id}/aprobar?usuarioId=${usuarioId}`);
    return data;
  },

  /**
   * Rechaza una devolución (solo admin).
   * @param {number} id - ID de la devolución
   * @param {number} usuarioId - ID del usuario que rechaza
   * @param {string} razon - Razón del rechazo
   * @returns {Promise<Object>} Devolución rechazada
   * @example
   * await devolucionesService.rechazar(1, 3, 'Producto en buen estado');
   */
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
  /**
   * Obtiene todos los detalles de devoluciones.
   * @returns {Promise<Array>} Lista de detalles de devoluciones
   * @example
   * const detalles = await detalleDevolucionesService.getAll();
   */
  getAll: async () => {
    const { data } = await adminApi.get('/detalle-devoluciones');
    return toArray(data);
  },

  /**
   * Obtiene los detalles de una devolución específica.
   * @param {number} devolucionId - ID de la devolución
   * @returns {Promise<Array>} Lista de detalles de la devolución
   * @example
   * const detalles = await detalleDevolucionesService.getByDevolucion(1);
   */
  getByDevolucion: async (devolucionId) => {
    const { data } = await adminApi.get(`/detalle-devoluciones/devolucion/${devolucionId}`);
    return toArray(data);
  },

  /**
   * Obtiene un detalle de devolución por su ID.
   * @param {number} id - ID del detalle
   * @returns {Promise<Object>} Detalle encontrado
   * @example
   * const detalle = await detalleDevolucionesService.getById(1);
   */
  getById: async (id) => {
    const { data } = await adminApi.get(`/detalle-devoluciones/${id}`);
    return data;
  },

  /**
   * Crea un nuevo detalle de devolución.
   * @param {Object} detalle - Datos del detalle a crear
   * @returns {Promise<Object>} Detalle creado
   * @example
   * const nuevo = await detalleDevolucionesService.create({
   *   devolucionId: 1,
   *   productoId: 10,
   *   cantidad: 2
   * });
   */
  create: async (detalle) => {
    const { data } = await adminApi.post('/detalle-devoluciones', detalle);
    return data;
  },

  /**
   * Actualiza un detalle de devolución existente.
   * @param {Object} detalle - Datos del detalle a actualizar
   * @returns {Promise<Object>} Detalle actualizado
   * @example
   * const actualizado = await detalleDevolucionesService.update({ detalleId: 1, cantidad: 3 });
   */
  update: async (detalle) => {
    const { data } = await adminApi.put('/detalle-devoluciones', detalle);
    return data;
  },

  /**
   * Elimina un detalle de devolución.
   * @param {number} id - ID del detalle a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await detalleDevolucionesService.delete(1);
   */
  delete: async (id) => {
    const { data } = await adminApi.delete(`/detalle-devoluciones/${id}`);
    return data;
  },
};
