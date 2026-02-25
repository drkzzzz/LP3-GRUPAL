import apiClient from './api';

/**
 * Servicio para gestionar Configuración Global de la Plataforma
 */
const configService = {
  // Obtener configuración global
  getConfig: async () => {
    const response = await apiClient.get('/superadmin/configuracion-global');
    return response.data;
  },

  // Actualizar configuración global
  updateConfig: async (data) => {
    const response = await apiClient.put('/superadmin/configuracion-global', data);
    return response.data;
  },

  // Obtener plantillas de email
  getEmailTemplates: async () => {
    const response = await apiClient.get('/superadmin/plantillas-email');
    return response.data;
  },

  // Actualizar plantilla de email
  updateEmailTemplate: async (id, data) => {
    const response = await apiClient.put(`/superadmin/plantillas-email/${id}`, data);
    return response.data;
  },

  // Obtener integraciones externas
  getIntegrations: async () => {
    const response = await apiClient.get('/superadmin/integraciones');
    return response.data;
  },

  // Actualizar integración
  updateIntegration: async (id, data) => {
    const response = await apiClient.put(`/superadmin/integraciones/${id}`, data);
    return response.data;
  },

  // Test de integración
  testIntegration: async (tipo) => {
    const response = await apiClient.post(`/superadmin/integraciones/${tipo}/test`);
    return response.data;
  },
};

export default configService;
