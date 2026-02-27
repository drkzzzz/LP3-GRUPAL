import { api } from '@/config/api';

export const adminAuthService = {
  login: async (email, password) => {
    const { data } = await api.post('/admin/auth/login', { email, password });
    return data; // { token, usuario, negocio }
  },
};
