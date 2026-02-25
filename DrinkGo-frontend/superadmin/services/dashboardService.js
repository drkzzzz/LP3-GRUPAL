import apiClient from './api';

/**
 * Servicio para obtener métricas del Dashboard de SuperAdmin
 */
const dashboardService = {
  // Obtener métricas principales
  getMetrics: async () => {
    const response = await apiClient.get('/superadmin/dashboard/metricas');
    return response.data;
  },

  // Obtener ingresos mensuales
  getMonthlyRevenue: async (year) => {
    const response = await apiClient.get('/superadmin/dashboard/ingresos-mensuales', {
      params: { year }
    });
    return response.data;
  },

  // Obtener negocios nuevos por mes
  getNewBusinesses: async (months = 6) => {
    const response = await apiClient.get('/superadmin/dashboard/negocios-nuevos', {
      params: { months }
    });
    return response.data;
  },

  // Obtener distribución por planes
  getPlanDistribution: async () => {
    const response = await apiClient.get('/superadmin/dashboard/distribucion-planes');
    return response.data;
  },

  // Obtener actividad en tiempo real
  getRealTimeActivity: async () => {
    const response = await apiClient.get('/superadmin/dashboard/actividad-tiempo-real');
    return response.data;
  },
};

export default dashboardService;
