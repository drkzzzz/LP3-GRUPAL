import { api } from '@/config/api';

/**
 * Servicio para gestión de planes de suscripción.
 * Maneja los planes disponibles para los negocios (Básico, Pro, Premium).
 */
export const planesService = {
  /**
   * Obtiene todos los planes de suscripción.
   * @returns {Promise<Array>} Lista de planes
   * @example
   * const planes = await planesService.getAll();
   */
  getAll: async () => {
    const { data } = await api.get('/planes-suscripcion');
    return data;
  },

  /**
   * Obtiene un plan por su ID.
   * @param {number} id - ID del plan
   * @returns {Promise<Object>} Plan encontrado
   * @example
   * const plan = await planesService.getById(1);
   */
  getById: async (id) => {
    const { data } = await api.get(`/planes-suscripcion/${id}`);
    return data;
  },

  /**
   * Crea un nuevo plan de suscripción.
   * @param {Object} plan - Datos del plan (nombre, precio, características)
   * @returns {Promise<Object>} Plan creado
   * @example
   * const nuevo = await planesService.create({
   *   nombre: 'Premium',
   *   precioMensual: 99.00,
   *   maxSedes: 10
   * });
   */
  create: async (plan) => {
    const { data } = await api.post('/planes-suscripcion', plan);
    return data;
  },

  /**
   * Actualiza un plan existente.
   * @param {Object} plan - Datos del plan a actualizar
   * @returns {Promise<Object>} Plan actualizado
   * @example
   * const actualizado = await planesService.update({ id: 1, precioMensual: 79.00 });
   */
  update: async (plan) => {
    const { data } = await api.put('/planes-suscripcion', plan);
    return data;
  },

  /**
   * Elimina un plan.
   * @param {number} id - ID del plan a eliminar
   * @returns {Promise<Object>} Resultado de la operación
   * @example
   * await planesService.delete(1);
   */
  delete: async (id) => {
    const { data } = await api.delete(`/planes-suscripcion/${id}`);
    return data;
  },

  /**
   * Desactiva un plan sin eliminarlo (RF-PLT-004).
   * @param {Object} plan - Plan a desactivar
   * @returns {Promise<Object>} Plan desactivado
   * @example
   * await planesService.deactivate({ id: 1, estaActivo: true });
   */
  deactivate: async (plan) => {
    const { data } = await api.put('/planes-suscripcion', { ...plan, estaActivo: false });
    return data;
  },

  /**
   * Reactiva un plan previamente desactivado.
   * @param {Object} plan - Plan a reactivar
   * @returns {Promise<Object>} Plan activado
   * @example
   * await planesService.activate({ id: 1, estaActivo: false });
   */
  activate: async (plan) => {
    const { data } = await api.put('/planes-suscripcion', { ...plan, estaActivo: true });
    return data;
  },
};
