import { api } from '@/config/api';

export const facturacionService = {
  getAll: async () => {
    const { data } = await api.get('/facturas-suscripcion');
    return data;
  },

  getById: async (id) => {
    const { data } = await api.get(`/facturas-suscripcion/${id}`);
    return data;
  },

  create: async (factura) => {
    const { data } = await api.post('/facturas-suscripcion', factura);
    return data;
  },

  update: async (factura) => {
    const { data } = await api.put('/facturas-suscripcion', factura);
    return data;
  },

  delete: async (id) => {
    const { data } = await api.delete(`/facturas-suscripcion/${id}`);
    return data;
  },

  // Marca una factura como pagada (RF-FAC-001)
  marcarPagada: async (factura) => {
    const payload = { ...factura, estado: 'pagada', pagadoEn: new Date().toISOString() };
    const { data } = await api.put('/facturas-suscripcion', payload);
    return data;
  },

  // Cancela una factura pendiente (RF-FAC-002)
  cancelar: async (factura) => {
    const payload = { ...factura, estado: 'cancelada' };
    const { data } = await api.put('/facturas-suscripcion', payload);
    return data;
  },

  // Reintenta cobro de factura vencida/fallida (RF-FAC-001)
  reintentar: async (factura) => {
    const payload = { ...factura, estado: 'pendiente' };
    const { data } = await api.put('/facturas-suscripcion', payload);
    return data;
  },
};
