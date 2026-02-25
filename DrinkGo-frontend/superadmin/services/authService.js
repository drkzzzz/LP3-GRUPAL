import apiClient from './api';

/**
 * Servicio para autenticación de SuperAdmin
 */
const authService = {
  // Login de SuperAdmin
  login: async (email, password) => {
    const response = await apiClient.post('/superadmin/auth/login', {
      email,
      password,
    });
    
    if (response.data.token) {
      localStorage.setItem('superadmin_token', response.data.token);
      localStorage.setItem('superadmin_user', JSON.stringify(response.data.usuario));
    }
    
    return response.data;
  },

  // Logout
  logout: () => {
    localStorage.removeItem('superadmin_token');
    localStorage.removeItem('superadmin_user');
  },

  // Obtener usuario actual
  getCurrentUser: () => {
    const userStr = localStorage.getItem('superadmin_user');
    return userStr ? JSON.parse(userStr) : null;
  },

  // Verificar si está autenticado
  isAuthenticated: () => {
    return !!localStorage.getItem('superadmin_token');
  },

  // Cambiar contraseña
  changePassword: async (currentPassword, newPassword) => {
    const response = await apiClient.post('/superadmin/auth/cambiar-password', {
      password_actual: currentPassword,
      password_nueva: newPassword,
    });
    return response.data;
  },

  // Recuperar contraseña
  forgotPassword: async (email) => {
    const response = await apiClient.post('/superadmin/auth/recuperar-password', { email });
    return response.data;
  },

  // Resetear contraseña con token
  resetPassword: async (token, newPassword) => {
    const response = await apiClient.post('/superadmin/auth/reset-password', {
      token,
      password_nueva: newPassword,
    });
    return response.data;
  },
};

export default authService;
