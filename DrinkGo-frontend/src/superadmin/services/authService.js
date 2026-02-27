import { api } from '@/config/api';

export const authService = {
  login: async (email, password) => {
    const { data } = await api.post('/superadmin/auth/login', { email, password });
    return data; // { token, usuario }
  },
};
