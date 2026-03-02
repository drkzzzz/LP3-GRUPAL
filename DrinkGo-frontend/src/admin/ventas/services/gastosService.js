import { api } from '@/config/api';

export const gastosService = {
  /* ─── Gastos externos del negocio ─── */
  getByNegocio: async (negocioId) => {
    const { data } = await api.get(`/gastos/negocio/${negocioId}`);
    return Array.isArray(data) ? data.filter(Boolean) : [];
  },

  create: async (gasto) => {
    const { data } = await api.post('/gastos', gasto);
    return data;
  },

  update: async (gasto) => {
    const { data } = await api.put('/gastos', gasto);
    return data;
  },

  remove: async (id) => {
    const { data } = await api.delete(`/gastos/${id}`);
    return data;
  },

  marcarPagado: async ({ id, metodoPago, referencia }) => {
    const params = {};
    if (metodoPago) params.metodoPago = metodoPago;
    if (referencia) params.referencia = referencia;
    const { data } = await api.patch(`/gastos/${id}/pagar`, null, { params });
    return data;
  },

  /* ─── Categorías de gasto del negocio ─── */
  getCategoriasByNegocio: async (negocioId) => {
    const { data } = await api.get(`/categorias-gasto/negocio/${negocioId}`);
    return Array.isArray(data) ? data.filter(Boolean) : [];
  },

  createCategoria: async (categoria) => {
    const { data } = await api.post('/categorias-gasto', categoria);
    return data;
  },

  /* ─── Facturas de suscripción del negocio (cobros del sistema) ─── */
  getFacturasByNegocio: async (negocioId) => {
    const { data } = await api.get(`/facturas-suscripcion/por-negocio/${negocioId}`);
    return Array.isArray(data) ? data.filter(Boolean) : [];
  },

  pagarFacturaSuscripcion: async ({ id, metodoPago, referencia }) => {
    const params = {};
    if (metodoPago) params.metodoPago = metodoPago;
    if (referencia) params.referencia = referencia;
    const { data } = await api.patch(`/facturas-suscripcion/${id}/pagar`, null, { params });
    return data;
  },
};
