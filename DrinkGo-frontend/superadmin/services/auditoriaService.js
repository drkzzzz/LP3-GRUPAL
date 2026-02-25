import apiClient from './api';

/**
 * Servicio para gestionar Auditoría y Logs del Sistema
 */
const auditoriaService = {
  // Obtener logs de auditoría con filtros
  getAll: async (params = {}) => {
    const response = await apiClient.get('/superadmin/auditoria', { params });
    return response.data;
  },

  // Obtener detalle de un evento de auditoría
  getById: async (id) => {
    const response = await apiClient.get(`/superadmin/auditoria/${id}`);
    return response.data;
  },

  // Exportar logs en formato específico
  exportLogs: async (params = {}, format = 'csv') => {
    const response = await apiClient.get('/superadmin/auditoria/exportar', {
      params: { ...params, format },
      responseType: 'blob',
    });
    return response.data;
  },

  // Obtener estadísticas de actividad
  getStats: async (params = {}) => {
    const response = await apiClient.get('/superadmin/auditoria/estadisticas', { params });
    return response.data;
  },

  // Detectar anomalías
  detectAnomalies: async (params = {}) => {
    const response = await apiClient.get('/superadmin/auditoria/anomalias', { params });
    return response.data;
  },
};

export default auditoriaService;
