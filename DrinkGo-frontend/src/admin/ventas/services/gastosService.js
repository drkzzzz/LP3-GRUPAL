import { adminApi } from '@/admin/services/adminApi';

export const gastosService = {
  /* ─── Gastos externos del negocio ─── */
  getByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/gastos/negocio/${negocioId}`);
    return Array.isArray(data) ? data.filter(Boolean) : [];
  },

  create: async (gasto) => {
    const { data } = await adminApi.post('/gastos', gasto);
    return data;
  },

  update: async (gasto) => {
    const { data } = await adminApi.put('/gastos', gasto);
    return data;
  },

  remove: async (id) => {
    const { data } = await adminApi.delete(`/gastos/${id}`);
    return data;
  },

  marcarPagado: async ({ id, metodoPago, referencia }) => {
    const params = {};
    if (metodoPago) params.metodoPago = metodoPago;
    if (referencia) params.referencia = referencia;
    const { data } = await adminApi.patch(`/gastos/${id}/pagar`, null, { params });
    return data;
  },

  subirComprobante: async ({ id, archivo }) => {
    const formData = new FormData();
    formData.append('archivo', archivo);
    const { data } = await adminApi.postForm(`/gastos/${id}/comprobante`, formData);
    return data;
  },

  eliminarComprobante: async (id) => {
    const { data } = await adminApi.delete(`/gastos/${id}/comprobante`);
    return data;
  },

  /* ─── Categorías de gasto ─── */
  getCategoriasByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/categorias-gasto/negocio/${negocioId}`);
    return Array.isArray(data) ? data.filter(Boolean) : [];
  },

  createCategoria: async (categoria) => {
    const { data } = await adminApi.post('/categorias-gasto', categoria);
    return data;
  },

  updateCategoria: async (categoria) => {
    const { data } = await adminApi.put('/categorias-gasto', categoria);
    return data;
  },

  deleteCategoria: async (id) => {
    const { data } = await adminApi.delete(`/categorias-gasto/${id}`);
    return data;
  },

  /* ─── Facturas de suscripción del negocio (cobros del sistema) ─── */
  getFacturasByNegocio: async (negocioId) => {
    const { data } = await adminApi.get(`/facturas-suscripcion/por-negocio/${negocioId}`);
    return Array.isArray(data) ? data.filter(Boolean) : [];
  },

  pagarFacturaSuscripcion: async ({ id, metodoPago, referencia }) => {
    const params = {};
    if (metodoPago) params.metodoPago = metodoPago;
    if (referencia) params.referencia = referencia;
    const { data } = await adminApi.patch(`/facturas-suscripcion/${id}/pagar`, null, { params });
    return data;
  },
};
