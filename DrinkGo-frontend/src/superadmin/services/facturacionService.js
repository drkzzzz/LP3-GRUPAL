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

  /** Genera automáticamente una factura para una suscripción (cargo mensual) */
  generar: async (suscripcionId) => {
    const { data } = await api.post(`/facturas-suscripcion/generar/${suscripcionId}`);
    return data;
  },

  /** Marca una factura como pagada */
  marcarPagada: async ({ id, metodoPago, referenciaPago }) => {
    const params = {};
    if (metodoPago) params.metodoPago = metodoPago;
    if (referenciaPago) params.referencia = referenciaPago;
    const { data } = await api.patch(`/facturas-suscripcion/${id}/pagar`, null, { params });
    return data;
  },

  /** Cancela/anula una factura pendiente */
  cancelar: async ({ id }) => {
    const { data } = await api.patch(`/facturas-suscripcion/${id}/cancelar`);
    return data;
  },

  /** Reintenta cobro: vuelve al estado pendiente */
  reintentar: async ({ id }) => {
    const { data } = await api.patch(`/facturas-suscripcion/${id}/pagar`, null, {
      params: { metodoPago: 'pendiente' },
    });
    return data;
  },
};
