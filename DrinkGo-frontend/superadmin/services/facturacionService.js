import apiClient from './api';

/**
 * Servicio para gestionar Facturación de Suscripciones
 */
const facturacionService = {
  // Obtener todas las facturas de suscripción
  getAll: async (params = {}) => {
    const response = await apiClient.get('/facturas-suscripcion', { params });
    return response.data;
  },

  // Obtener una factura por ID
  getById: async (id) => {
    const response = await apiClient.get(`/facturas-suscripcion/${id}`);
    return response.data;
  },

  // Crear factura manual
  create: async (data) => {
    const response = await apiClient.post('/facturas-suscripcion', data);
    return response.data;
  },

  // Marcar factura como pagada
  markAsPaid: async (id, data) => {
    const response = await apiClient.patch(`/facturas-suscripcion/${id}/marcar-pagada`, data);
    return response.data;
  },

  // Anular factura
  cancel: async (id, motivo) => {
    const response = await apiClient.patch(`/facturas-suscripcion/${id}/anular`, { motivo });
    return response.data;
  },

  // Enviar recordatorio de pago
  sendReminder: async (id) => {
    const response = await apiClient.post(`/facturas-suscripcion/${id}/enviar-recordatorio`);
    return response.data;
  },

  // Obtener facturas morosas
  getOverdue: async () => {
    const response = await apiClient.get('/facturas-suscripcion/morosas');
    return response.data;
  },

  // Obtener reporte de facturación
  getReport: async (params = {}) => {
    const response = await apiClient.get('/facturas-suscripcion/reporte', { params });
    return response.data;
  },
};

export default facturacionService;
