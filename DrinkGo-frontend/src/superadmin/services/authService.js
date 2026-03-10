import { api } from '@/config/api';

/**
 * Servicio de autenticación para superadmin.
 * Maneja el login del superadmin del sistema.
 */
export const authService = {
  /**
   * Autentica un superadmin con email y contraseña.
   * @param {string} email - Email del superadmin
   * @param {string} password - Contraseña
   * @returns {Promise<Object>} Objeto con token JWT y datos del usuario
   * @example
   * const { token, usuario } = await authService.login('admin@drinkgo.com', 'password');
   */
  login: async (email, password) => {
    const { data } = await api.post('/superadmin/auth/login', { email, password });
    return data; // { token, usuario }
  },
};
