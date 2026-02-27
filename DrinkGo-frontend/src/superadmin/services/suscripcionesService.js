import { api } from '@/config/api';

export const suscripcionesService = {
  getAll: async () => {
    const { data } = await api.get('/suscripciones');
    return data;
  },

  getById: async (id) => {
    const { data } = await api.get(`/suscripciones/${id}`);
    return data;
  },

  create: async (suscripcion) => {
    const { data } = await api.post('/suscripciones', suscripcion);
    return data;
  },

  update: async (suscripcion) => {
    const { data } = await api.put('/suscripciones', suscripcion);
    return data;
  },

  delete: async (id) => {
    const { data } = await api.delete(`/suscripciones/${id}`);
    return data;
  },
};
