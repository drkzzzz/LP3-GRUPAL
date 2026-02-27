import { api } from '@/config/api';

export const auditoriaService = {
  getAll: async () => {
    const { data } = await api.get('/registros-auditoria');
    return data;
  },

  getById: async (id) => {
    const { data } = await api.get(`/registros-auditoria/${id}`);
    return data;
  },
};
