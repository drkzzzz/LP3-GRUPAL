import apiClient from './api';

/**
 * Servicio para gestionar Planes de Suscripción
 */
const planesService = {
  // Obtener todos los planes
  getAll: async (includeInactive = false) => {
    const response = await apiClient.get('/planes-suscripcion', {
      params: { incluir_inactivos: includeInactive }
    });
    return response.data;
  },

  // Obtener un plan por ID
  getById: async (id) => {
    const response = await apiClient.get(`/planes-suscripcion/${id}`);
    return response.data;
  },

  // Crear un nuevo plan
  create: async (data) => {
    const response = await apiClient.post('/planes-suscripcion', data);
    return response.data;
  },

  // Actualizar un plan
  update: async (id, data) => {
    const response = await apiClient.put(`/planes-suscripcion/${id}`, data);
    return response.data;
  },

  // Activar/Desactivar un plan
  toggleStatus: async (id) => {
    const response = await apiClient.patch(`/planes-suscripcion/${id}/toggle-status`);
    return response.data;
  },

  // Obtener negocios suscritos a un plan
  getSubscribedBusinesses: async (planId) => {
    const response = await apiClient.get(`/planes-suscripcion/${planId}/negocios`);
    return response.data;
  },
};

export default planesService;
