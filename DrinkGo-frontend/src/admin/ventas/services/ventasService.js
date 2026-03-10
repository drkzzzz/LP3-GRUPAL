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
  /**
   * Lista todas las ventas de un negocio específico.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de ventas del negocio
   * @example
   * const ventas = await ventasService.getByNegocio(5);
   */
  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/ventas/negocio/${negocioId}`);
    return toArray(data);
  },

  /**
   * Lista todas las ventas de una sesión de caja específica.
   * @param {number} sesionCajaId - ID de la sesión de caja
   * @returns {Promise<Array>} Lista de ventas de la sesión
   * @example
   * const ventas = await ventasService.getBySesion(10);
   */
  getBySesion: async (sesionCajaId) => {
    const { data } = await adminApi.get(`/pos/ventas/sesion/${sesionCajaId}`);
    return toArray(data);
  },

  /**
   * Obtiene una venta específica por ID y negocio.
   * @param {number} ventaId - ID de la venta
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Object>} Venta encontrada
   * @example
   * const venta = await ventasService.getById(100, 5);
   */
  getById: async (ventaId, negocioId) => {
    const { data } = await adminApi.get(`/pos/ventas/${ventaId}/negocio/${negocioId}`);
    return data;
  },

  /**
   * Obtiene el detalle (productos) de una venta específica.
   * @param {number} ventaId - ID de la venta
   * @returns {Promise<Array>} Detalle de la venta (productos)
   * @example
   * const detalle = await ventasService.getDetalle(100);
   */
  getDetalle: async (ventaId) => {
    const { data } = await adminApi.get(`/pos/ventas/${ventaId}/detalle`);
    return toArray(data);
  },

  /**
   * Obtiene los pagos asociados a una venta.
   * @param {number} ventaId - ID de la venta
   * @returns {Promise<Array>} Lista de pagos de la venta
   * @example
   * const pagos = await ventasService.getPagos(100);
   */
  getPagos: async (ventaId) => {
    const { data } = await adminApi.get(`/pos/ventas/${ventaId}/pagos`);
    return toArray(data);
  },

  /**
   * Crea una nueva venta en el sistema POS.
   * @param {Object} ventaData - Datos de la venta
   * @param {number} ventaData.negocioId - ID del negocio
   * @param {number} ventaData.sesionCajaId - ID de la sesión de caja
   * @param {Array} ventaData.productos - Productos de la venta
   * @param {Array} ventaData.pagos - Métodos de pago utilizados
   * @returns {Promise<Object>} Venta creada
   * @example
   * const venta = await ventasService.crear({
   *   negocioId: 5,
   *   sesionCajaId: 10,
   *   productos: [...],
   *   pagos: [...]
   * });
   */
  crear: async (ventaData) => {
    const { data } = await adminApi.post('/pos/ventas', ventaData);
    return data;
  },

  /**
   * Anula una venta existente.
   * @param {Object} anularData - Datos para anulación
   * @param {number} anularData.ventaId - ID de la venta a anular
   * @param {string} anularData.motivo - Motivo de anulación
   * @param {number} anularData.usuarioId - ID del usuario que anula
   * @returns {Promise<Object>} Resultado de la anulación
   * @example
   * const resultado = await ventasService.anular({
   *   ventaId: 100,
   *   motivo: 'Error en registro',
   *   usuarioId: 3
   * });
   */
  anular: async (anularData) => {
    const { data } = await adminApi.post('/pos/ventas/anular', anularData);
    return data;
  },

  /**
   * Obtiene los métodos de pago disponibles para un negocio.
   * @param {number} negocioId - ID del negocio
   * @returns {Promise<Array>} Lista de métodos de pago
   * @example
   * const metodos = await ventasService.getMetodosPago(5);
   */
  getMetodosPago: async (negocioId) => {
    const { data } = await adminApi.get(`/pos/metodos-pago/negocio/${negocioId}`);
    return toArray(data);
  },
};
