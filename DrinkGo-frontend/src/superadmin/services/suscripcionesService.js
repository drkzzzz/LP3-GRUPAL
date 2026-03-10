import { api } from '@/config/api';

/**
 * Servicio para gestión de suscripciones de negocios.
 * Maneja las suscripciones activas de los negocios a planes.
 */
export const suscripcionesService = {
  /**
   * Obtiene todas las suscripciones.
   * @returns {Promise<Array>} Lista de suscripciones
   * @example
   * const suscripciones = await suscripcionesService.getAll();
   */
  getAll: async () => {
    const { data } = await api.get('/suscripciones');
    return data;
  },

  /**
   * Obtiene una suscripción por su ID.
   * @param {number} id - ID de la suscripción
   * @returns {Promise<Object>} Suscripción encontrada
   * @example
   * const suscripcion = await suscripcionesService.getById(1);
   */
  getById: async (id) => {
    const { data } = await api.get(`/suscripciones/${id}`);
    return data;
  },

  /**
   * Crea una nueva suscripción.
   * @param {Object} suscripcion - Datos de la suscripción (negocioId, planId, fechas)
   * @returns {Promise<Object>} Suscripción creada
   * @example
   * const nueva = await suscripcionesService.create({
   *   negocioId: 5,
   *   planId: 2,
   *   fechaInicio: '2024-01-01'
   * });
   */
  create: async (suscripcion) => {
    const { data } = await api.post('/suscripciones', suscripcion);
    return data;
  },

  /**
   * Actualiza una suscripción existente.
   * @param {Object} suscripcion - Datos de la suscripción a actualizar
   * @returns {Promise<Object>} Suscripción actualizada
   * @example
   * const actualizada = await suscripcionesService.update({ id: 1, ... });
   */
  update: async (suscripcion) => {
    const { data } = await api.put('/suscripciones', suscripcion);
    return data;
  },

  /**
   * Elimina una suscripción.
   * @param {number} id - ID de la suscripción a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await suscripcionesService.delete(1);
   */
  delete: async (id) => {
    const { data } = await api.delete(`/suscripciones/${id}`);
    return data;
  },
};
