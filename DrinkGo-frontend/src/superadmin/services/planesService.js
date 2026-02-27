import { api } from '@/config/api';

export const planesService = {
  getAll: async () => {
    const { data } = await api.get('/planes-suscripcion');
    return data;
  },

  getById: async (id) => {
    const { data } = await api.get(`/planes-suscripcion/${id}`);
    return data;
  },

  create: async (plan) => {
    const { data } = await api.post('/planes-suscripcion', plan);
    return data;
  },

  update: async (plan) => {
    const { data } = await api.put('/planes-suscripcion', plan);
    return data;
  },

  delete: async (id) => {
    const { data } = await api.delete(`/planes-suscripcion/${id}`);
    return data;
  },

  // Desactiva un plan sin eliminarlo (RF-PLT-004)
  deactivate: async (plan) => {
    const { data } = await api.put('/planes-suscripcion', { ...plan, estaActivo: false });
    return data;
  },

  // Reactiva un plan previamente desactivado
  activate: async (plan) => {
    const { data } = await api.put('/planes-suscripcion', { ...plan, estaActivo: true });
    return data;
  },
};
