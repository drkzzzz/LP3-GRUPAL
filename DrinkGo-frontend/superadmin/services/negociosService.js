import apiClient from './api';

/**
 * Servicio para gestionar Negocios (Licorerías/Tenants)
 */
const negociosService = {
  // Obtener todos los negocios con filtros opcionales
  getAll: async (params = {}) => {
    const response = await apiClient.get('/negocios', { params });
    return response.data;
  },

  // Obtener un negocio por ID
  getById: async (id) => {
    const response = await apiClient.get(`/negocios/${id}`);
    return response.data;
  },

  // Crear un nuevo negocio
  create: async (data) => {
    const response = await apiClient.post('/negocios', data);
    return response.data;
  },

  // Actualizar un negocio
  update: async (id, data) => {
    const response = await apiClient.put(`/negocios/${id}`, data);
    return response.data;
  },

  // Suspender un negocio
  suspend: async (id, motivo) => {
    const response = await apiClient.patch(`/negocios/${id}/suspender`, { motivo });
    return response.data;
  },

  // Reactivar un negocio
  reactivate: async (id) => {
    const response = await apiClient.patch(`/negocios/${id}/reactivar`);
    return response.data;
  },

  // Obtener estadísticas de un negocio
  getStats: async (id) => {
    const response = await apiClient.get(`/negocios/${id}/estadisticas`);
    return response.data;
  },

  // Obtener sedes de un negocio
  getSedes: async (negocioId) => {
    const response = await apiClient.get(`/negocios/${negocioId}/sedes`);
    return response.data;
  },
};

export default negociosService;
