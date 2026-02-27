import { api } from '@/config/api';

export const negociosService = {
  /* ─── Negocios CRUD ─── */
  getAll: async () => {
    const { data } = await api.get('/negocios');
    return data;
  },

  getById: async (id) => {
    const { data } = await api.get(`/negocios/${id}`);
    return data;
  },

  create: async (negocio) => {
    const { data } = await api.post('/negocios', negocio);
    return data;
  },

  update: async (negocio) => {
    const { data } = await api.put('/negocios', negocio);
    return data;
  },

  delete: async (id) => {
    const { data } = await api.delete(`/negocios/${id}`);
    return data;
  },

  /* ─── Sedes CRUD ─── */
  getAllSedes: async () => {
    const { data } = await api.get('/sedes');
    return data;
  },

  createSede: async (sede) => {
    const { data } = await api.post('/sedes', sede);
    return data;
  },

  updateSede: async (sede) => {
    const { data } = await api.put('/sedes', sede);
    return data;
  },

  deleteSede: async (id) => {
    const { data } = await api.delete(`/sedes/${id}`);
    return data;
  },

  /* ─── Suscripciones ─── */
  getAllSuscripciones: async () => {
    const { data } = await api.get('/suscripciones');
    return data;
  },

  createSuscripcion: async (suscripcion) => {
    const { data } = await api.post('/suscripciones', suscripcion);
    return data;
  },

  updateSuscripcion: async (suscripcion) => {
    const { data } = await api.put('/suscripciones', suscripcion);
    return data;
  },

  /* ─── Planes de suscripción ─── */
  getAllPlanes: async () => {
    const { data } = await api.get('/planes-suscripcion');
    return data;
  },
};
