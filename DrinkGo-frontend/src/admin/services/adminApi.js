/**
 * Instancia Axios dedicada para el panel Admin.
 * Lee el JWT desde useAdminAuthStore (no desde el store de SuperAdmin).
 * Redirige a /admin/login cuando recibe un 401.
 */
import axios from 'axios';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

export const adminApi = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/restful',
  headers: { 'Content-Type': 'application/json' },
});

/* ─── Request: adjuntar token JWT del admin ─── */
adminApi.interceptors.request.use(
  (config) => {
    const token = useAdminAuthStore.getState().token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

/* ─── Response: manejar 401 ─── */
adminApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const isLoginPage = window.location.pathname.includes('/login');
      if (!isLoginPage) {
        useAdminAuthStore.getState().logout();
        window.location.href = '/admin/login';
      }
    }
    return Promise.reject(error);
  },
);
