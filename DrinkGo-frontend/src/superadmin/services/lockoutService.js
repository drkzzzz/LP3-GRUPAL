import { api } from '@/config/api';

export const lockoutService = {
  getTodos: async () => {
    const { data } = await api.get('/superadmin/todos-usuarios');
    return data;
  },

  getBloqueados: async () => {
    const { data } = await api.get('/superadmin/usuarios-bloqueados');
    return data;
  },

  desbloquearPlataforma: async (id) => {
    const { data } = await api.patch(`/superadmin/usuarios-plataforma/${id}/desbloquear`);
    return data;
  },

  desbloquearAdmin: async (id) => {
    const { data } = await api.patch(`/superadmin/usuarios/${id}/desbloquear`);
    return data;
  },
};
